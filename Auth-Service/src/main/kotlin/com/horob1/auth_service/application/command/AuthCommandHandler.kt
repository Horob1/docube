package com.horob1.auth_service.application.command

import com.horob1.auth_service.api.dto.request.RegisterDto
import com.horob1.auth_service.api.dto.request.toCreateUser
import com.horob1.auth_service.api.dto.response.LoginResponseDto
import com.horob1.auth_service.api.exception.BannedUserRequest
import com.horob1.auth_service.api.exception.FailedLoginLimit
import com.horob1.auth_service.api.exception.InvalidIdentityInfo
import com.horob1.auth_service.api.exception.InvalidToken
import com.horob1.auth_service.api.exception.TOO_MANY_REQUESTS
import com.horob1.auth_service.api.exception.UserExistedRequest
import com.horob1.auth_service.api.exception.UserNotFound
import com.horob1.auth_service.api.exception.WrongOtp
import com.horob1.auth_service.domain.repository.AuthRepository
import com.horob1.auth_service.infrastructure.GoogleAuthClient
import com.horob1.auth_service.infrastructure.producer.AuthEventProducer
import com.horob1.auth_service.infrastructure.repository.RedisStringService
import com.horob1.auth_service.util.jwt.JwtTokenManager
import com.horob1.common_service.api.dto.request.CreateUserDto
import com.horob1.common_service.api.exception.AppError
import com.horob1.common_service.api.exception.AppException
import com.horob1.common_service.enums.EmailType
import com.horob1.common_service.util.OtpGenerator
import com.horob1.common_service.enums.UserStatus
import com.horob1.common_service.enums.TokenType
import com.horob1.common_service.kafka.event.auth.OtpEmailEvent
import com.horob1.common_service.kafka.event.auth.UserIdEvent
import com.horob1.common_service.util.sha256
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class AuthCommandHandler(
    private val authRepository: AuthRepository,
    private val passwordEncoder: PasswordEncoder,
    private val redisStringService: RedisStringService,
    private val jwtTokenManager: JwtTokenManager,
    private val authEventProducer: AuthEventProducer,
    private val googleAuthClient: GoogleAuthClient
) {
    companion object {
        const val MAX_FAILED_PASSWORD_ATTEMPT = 5
        const val PREFIX_FAILED_PASSWORD_ATTEMPT = "FAILED_PASSWORD_ATTEMPT"
        const val PREFIX_2FA_TOKEN = "2FA_TOKEN"
        const val PREFIX_VERIFY_EMAIL_TOKEN = "VERIFY_EMAIL_TOKEN"
        const val LIMIT_VERIFY_EMAIL = "LIMIT_VERIFY_EMAIL"
    }
    fun login(email: String, password: String, ua: String): LoginResponseDto {
        val failedPasswordAttemptCount = calcFailedPasswordAttemptCount(email)

        if (failedPasswordAttemptCount >= MAX_FAILED_PASSWORD_ATTEMPT) {
            throw AppException(FailedLoginLimit)
        }

        val userDto = authRepository.findIdentityByEmail(
            email
        ) ?: throw AppException(AppError.AuthenticationFailed)

        if (!passwordEncoder.matches(password, userDto.password)) {
            saveFailedPasswordAttemptCount(email, failedPasswordAttemptCount + 1)
            throw AppException(InvalidIdentityInfo)
        }

        if (userDto.status == UserStatus.BAN) {
            throw AppException(BannedUserRequest)
        }

        if (userDto.status == UserStatus.PENDING) {
            // Gen otp and token
            val otp = OtpGenerator.generateOtp()

            // Save cache
            saveVerifyEmailOtp(email, otp)

            // Send message
            authEventProducer.sendVerifyEmailEvent(
                OtpEmailEvent(
                    userId = userDto.id,
                    email = email,
                    otp = otp,
                    name = "${userDto.firstName} ${userDto.lastName}",
                    emailType = EmailType.VERIFICATION,
                )
            )

            // Return
            return LoginResponseDto(
                accessToken = jwtTokenManager.generateToken(
                    subject = userDto.id,
                    tokenType = TokenType.EMAIL_VERIFY,
                ),
                refreshToken = null,
                accessTokenType = TokenType.EMAIL_VERIFY.name,
                clientId = null
            )
        }

        // Check 2fa
        if (userDto.is2FAEnabled) {
            // Gen otp and token
            val otp = OtpGenerator.generateOtp()

            // Save to cache
            save2FAOtp(email, otp)

            // Send message
            authEventProducer.sendVerifyEmailEvent(
                OtpEmailEvent(
                    userId = userDto.id,
                    email = email,
                    otp = otp,
                    name = "${userDto.firstName} ${userDto.lastName}",
                    emailType = EmailType.TWO_FACTOR_AUTH,
                )
            )

            // Gen access token
            return LoginResponseDto(
                accessToken = jwtTokenManager.generateToken(
                    subject = userDto.id,
                    tokenType = TokenType.TWO_FA_VERIFY,
                ),
                refreshToken = null,
                accessTokenType = TokenType.TWO_FA_VERIFY.name,
                clientId = null
            )
        }

        // gen token
        return genLoginResponse(userDto.id, ua)
    }

    fun loginGoogle(token: String, ua: String): LoginResponseDto {
        val payload = googleAuthClient.verify(token)

        val userDto =
            authRepository.findIdentityByEmail(email = payload.email)

        var userId: String

        if(userDto == null) {
            val createUserDto = CreateUserDto(
                email = payload.email,
                firstName = "User",
                lastName = UUID.randomUUID().toString(),
                password = passwordEncoder.encode(
                    UUID.randomUUID().toString(),
                )
            )
            userId = authRepository.createUser(createUserDto)
        } else userId = userDto.id

        if(userDto?.status == UserStatus.BAN) {
            throw AppException(BannedUserRequest)
        }

        //Gen token
        return genLoginResponse(userId, ua)
    }

    fun registerUser(
        dto: RegisterDto
    ) {
        val existedUser = authRepository.findIdentityByEmail(email = dto.email)

        if(existedUser != null) {
            throw AppException(UserExistedRequest)
        }

        val createUserDto = dto.toCreateUser().copy(
            password = passwordEncoder.encode(dto.password),
        )

        authRepository.createUser(createUserDto)
    }

    fun refreshToken(token: String, ua: String): LoginResponseDto {
        val payload = jwtTokenManager.validateAndParse(
            token = token,
            tokenType = TokenType.REFRESH,
        )

        val oldToken = authRepository.findToken(
            sha256(
                token
            )
        )

        if(oldToken == null) {
            throw AppException(InvalidToken)
        }

        if(payload.subject != oldToken.userId) {
            throw AppException(InvalidToken)
        }

        val newToken = jwtTokenManager.generateToken(
            subject = oldToken.userId,
            tokenType = TokenType.REFRESH,
            expiresIn = TokenType.REFRESH.defaultExpire
        )

        oldToken.token = sha256(newToken)
        oldToken.ua = ua

        authRepository.saveToken(oldToken)

        return LoginResponseDto(
            accessToken = jwtTokenManager.generateToken(
                subject = oldToken.userId,
                tokenType = TokenType.ACCESS,
            ),
            refreshToken = newToken,
            accessTokenType = TokenType.ACCESS.name,
            clientId = oldToken.id.toString()
        )

    }

    fun sendVerifyEmailOtp(userId: String) {
        val userDto = authRepository.findIdentityByUserId(userId) ?: throw AppException(UserNotFound)
        if(isLimitEmailValidation(email = userDto.email)) {
            throw AppException(TOO_MANY_REQUESTS)
        }
        setLimitEmailValidation(email = userDto.email)

        val otp = OtpGenerator.generateOtp()
        saveVerifyEmailOtp(userDto.email, otp)

        // Send message
        authEventProducer.sendVerifyEmailEvent(
            OtpEmailEvent(
                userId = userDto.id,
                email = userDto.email,
                otp = otp,
                name = "${userDto.firstName} ${userDto.lastName}",
                emailType = EmailType.VERIFICATION,
            )
        )
    }

    fun verifyEmail(userId: String, otp: String) {
        val userDto = authRepository.findIdentityByUserId(userId) ?: throw AppException(UserNotFound)
        val savedOtp = getVerifyEmailOtp(userDto.email) ?: throw AppException(WrongOtp)
        if(
            !passwordEncoder.matches(
                otp,
                savedOtp
            )
        ) throw AppException(WrongOtp)
        // Update status
        authEventProducer.sendVerifiedEmailEvent(
            UserIdEvent(
                userId = userDto.id,
            )
        )
    }

    private fun genLoginResponse(userId: String, ua: String): LoginResponseDto {
        val rfToken = jwtTokenManager.generateToken(
            subject = userId,
            tokenType = TokenType.REFRESH,
            expiresIn = TokenType.REFRESH.defaultExpire
        )
        val refreshToken = authRepository.saveNewRefreshToken(
            refreshToken = sha256(rfToken),
            userId = userId,
            ua = ua
        )

        // save refreshToken
        return LoginResponseDto(
            accessToken = jwtTokenManager.generateToken(
                subject = userId,
                tokenType = TokenType.ACCESS,
            ),
            refreshToken = rfToken,
            accessTokenType = TokenType.ACCESS.name,
            clientId = refreshToken.id.toString()
        )
    }

    private fun getVerifyEmailOtp(email: String): String? {
        return redisStringService.getValue(
            getVerifyEmailOtpKey(
                email
            )
        )
    }

    private fun saveVerifyEmailOtp(email: String, otp: String) {
        redisStringService.saveValue(getVerifyEmailOtpKey(email), passwordEncoder.encode(otp), Duration.ofMinutes(15))
    }

    private fun getVerifyEmailOtpKey(email: String): String {
        return "$PREFIX_VERIFY_EMAIL_TOKEN:${email}"
    }

    private fun calcFailedPasswordAttemptCount(email: String): Int {
        val countString = redisStringService.getValue(getFailedPasswordAttemptCountKey(email))
        return countString?.toIntOrNull() ?: 0
    }

    private fun saveFailedPasswordAttemptCount(email: String, count: Int) {
        redisStringService.saveValue(getFailedPasswordAttemptCountKey(email), count.toString(), Duration.ofMinutes(15))
    }

    private fun getFailedPasswordAttemptCountKey(email: String): String {
        return "$PREFIX_FAILED_PASSWORD_ATTEMPT:${email}"
    }

    private fun save2FAOtp(email: String, otp: String) {
        redisStringService.saveValue(get2FAOtpKey(email), passwordEncoder.encode(otp), Duration.ofMinutes(15))
    }

    private fun get2FAOtpKey(email: String): String {
        return "$PREFIX_2FA_TOKEN:${email}"
    }

    private fun getLimitEmailValidationKey(email: String): String {
        return "$LIMIT_VERIFY_EMAIL:${email}"
    }

    private fun isLimitEmailValidation(email: String): Boolean {
        return redisStringService.getValue(getLimitEmailValidationKey(email)) != null
    }

    private fun setLimitEmailValidation(email: String) {
        redisStringService.saveValue(
            getLimitEmailValidationKey(email),
            "1",
            Duration.ofMinutes(1)
        )
    }

}