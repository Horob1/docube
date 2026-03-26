package com.horob1.auth_service.service.command

import com.horob1.auth_service.api.dto.request.RegisterDto
import com.horob1.auth_service.api.dto.response.LoginResponseDto
import com.horob1.auth_service.api.exception.BannedUserRequest
import com.horob1.auth_service.api.exception.PendingUserRequest
import com.horob1.auth_service.api.exception.FailedLoginLimit
import com.horob1.auth_service.api.exception.InvalidIdentityInfo
import com.horob1.auth_service.api.exception.InvalidToken
import com.horob1.auth_service.api.exception.TOO_MANY_REQUESTS
import com.horob1.auth_service.api.exception.UserExistedRequest
import com.horob1.auth_service.api.exception.UserNotFound
import com.horob1.auth_service.api.exception.WrongOtp
import com.horob1.auth_service.domain.model.token.Token
import com.horob1.auth_service.domain.model.user.User
import com.horob1.auth_service.domain.repository.TokenRepository
import com.horob1.auth_service.domain.repository.UserRepository
import com.horob1.auth_service.infrastructure.GoogleAuthClient
import com.horob1.auth_service.infrastructure.producer.AuthEventProducer
import com.horob1.auth_service.infrastructure.repository.RedisStringRepository
import com.horob1.auth_service.service.TotpService
import com.horob1.auth_service.util.jwt.JwtTokenManager
import com.horob1.auth_service.shared.exception.AppError
import com.horob1.auth_service.shared.exception.AppException
import com.horob1.auth_service.shared.enums.EmailType
import com.horob1.auth_service.shared.util.OtpGenerator
import com.horob1.auth_service.shared.enums.UserStatus
import com.horob1.auth_service.shared.enums.TokenType
import com.horob1.auth_service.shared.kafka.event.OtpEmailEvent
import com.horob1.auth_service.shared.util.sha256
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class AuthCommandHandler(
    private val passwordEncoder: PasswordEncoder,
    private val redisStringRepository: RedisStringRepository,
    private val jwtTokenManager: JwtTokenManager,
    private val authEventProducer: AuthEventProducer,
    private val googleAuthClient: GoogleAuthClient,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val totpService: TotpService,
) {
    companion object {
        const val MAX_FAILED_PASSWORD_ATTEMPT = 5
        const val PREFIX_FAILED_PASSWORD_ATTEMPT = "FAILED_PASSWORD_ATTEMPT"
        const val PREFIX_VERIFY_EMAIL_TOKEN = "VERIFY_EMAIL_TOKEN"
        const val PREFIX_LIMIT_VERIFY_EMAIL = "LIMIT_VERIFY_EMAIL"
        const val PREFIX_LIMIT_FORGOT_PASSWORD = "LIMIT_FORGOT_PASSWORD"
        const val PREFIX_FORGOT_PASSWORD_OTP = "FORGOT_PASSWORD"
    }

    fun login(email: String, password: String, ua: String): LoginResponseDto {
        val failedPasswordAttemptCount = calcFailedPasswordAttemptCount(email)

        if (failedPasswordAttemptCount >= MAX_FAILED_PASSWORD_ATTEMPT) {
            throw AppException(FailedLoginLimit)
        }

        val user = userRepository.findByEmail(email) ?: throw AppException(AppError.AuthenticationFailed)

        if (!passwordEncoder.matches(password, user.password)) {
            saveFailedPasswordAttemptCount(email, failedPasswordAttemptCount + 1)
            throw AppException(InvalidIdentityInfo)
        }

        if (user.status == UserStatus.BAN) {
            throw AppException(BannedUserRequest)
        }

        if (user.status == UserStatus.PENDING) {
            // Gen otp and token
            val otp = OtpGenerator.generateOtp()

            // Save cache
            saveVerifyEmailOtp(email, otp)

            // Send message
            authEventProducer.sendVerifyEmailEvent(
                OtpEmailEvent(
                    userId = user.id.toString(),
                    email = email,
                    otp = otp,
                    name = "${user.firstName} ${user.lastName}",
                    emailType = EmailType.VERIFICATION,
                )
            )

            // Return
            return LoginResponseDto(
                accessToken = jwtTokenManager.generateToken(
                    subject = user.id.toString(),
                    tokenType = TokenType.EMAIL_VERIFY,
                ),
                refreshToken = null,
                accessTokenType = TokenType.EMAIL_VERIFY.name,
                clientId = null,
                userId = user.id.toString()
            )
        }

        // Check 2fa (TOTP-based - no email needed)
        if (user.is2FAEnabled && user.totpSecret != null) {
            return LoginResponseDto(
                accessToken = jwtTokenManager.generateToken(
                    subject = user.id.toString(),
                    tokenType = TokenType.TWO_FA_VERIFY,
                ),
                refreshToken = null,
                accessTokenType = TokenType.TWO_FA_VERIFY.name,
                clientId = null,
                userId = user.id.toString()
            )
        }

        // gen token
        return genLoginResponse(user.id!!, ua)
    }

    fun loginGoogle(token: String, ua: String): LoginResponseDto {
        val payload = googleAuthClient.verify(token)

        var user = userRepository.findByEmail(payload.email)

        if (user == null) {
            user = userRepository.save(
                User(
                    email = payload.email,
                    firstName = payload["given_name"]?.toString() ?: "USER",
                    lastName = payload["family_name"]?.toString() ?: "GOOGLE",
                    password = passwordEncoder.encode(UUID.randomUUID().toString()),
                    status = UserStatus.ACTIVE,
                )
            )
        }

        if (user.status == UserStatus.BAN) {
            throw AppException(BannedUserRequest)
        }

        if (user.status == UserStatus.PENDING) {
            user.previousStatus = user.status
            user.status = UserStatus.ACTIVE
            userRepository.save(user)
        }

        if (user.is2FAEnabled && user.totpSecret != null) {
            return LoginResponseDto(
                accessToken = jwtTokenManager.generateToken(
                    subject = user.id.toString(),
                    tokenType = TokenType.TWO_FA_VERIFY,
                ),
                refreshToken = null,
                accessTokenType = TokenType.TWO_FA_VERIFY.name,
                clientId = null,
                userId = user.id.toString()
            )
        }

        return genLoginResponse(user.id!!, ua)
    }

    fun registerUser(
        dto: RegisterDto
    ) {
        val existedUser = userRepository.findByEmail(email = dto.email)

        if (existedUser != null) {
            throw AppException(UserExistedRequest)
        }

        userRepository.save(
            User(
                email = dto.email,
                firstName = dto.firstName,
                lastName = dto.lastName,
                password = passwordEncoder.encode(dto.password),
                phoneNumber = dto.phoneNumber,
                address = dto.address,
                status = UserStatus.PENDING,
            )
        )
    }

    fun refreshToken(token: String, ua: String): LoginResponseDto {
        val payload = jwtTokenManager.validateAndParse(
            token = token,
            tokenType = TokenType.REFRESH,
        )

        val oldToken = tokenRepository.findTokenByToken(
            sha256(
                token
            )
        )

        if (oldToken == null) {
            throw AppException(InvalidToken)
        }

        if (payload.subject != oldToken.userId.toString()) {
            throw AppException(InvalidToken)
        }

        val newToken = jwtTokenManager.generateToken(
            subject = oldToken.userId.toString(),
            tokenType = TokenType.REFRESH,
            expiresIn = TokenType.REFRESH.defaultExpire
        )

        oldToken.token = sha256(newToken)
        oldToken.ua = ua

        tokenRepository.save(oldToken)

        return LoginResponseDto(
            accessToken = jwtTokenManager.generateToken(
                subject = oldToken.userId.toString(),
                tokenType = TokenType.ACCESS,
            ),
            refreshToken = newToken,
            accessTokenType = TokenType.ACCESS.name,
            clientId = oldToken.id.toString(),
            userId = oldToken.userId.toString()
        )

    }

    fun sendVerifyEmailOtp(userId: UUID) {
        val user = userRepository.findById(userId) ?: throw AppException(UserNotFound)
        if (isLimitEmailValidation(email = user.email)) {
            throw AppException(TOO_MANY_REQUESTS)
        }
        setLimitEmailValidation(email = user.email)

        val otp = OtpGenerator.generateOtp()
        saveVerifyEmailOtp(user.email, otp)

        // Send message
        authEventProducer.sendVerifyEmailEvent(
            OtpEmailEvent(
                userId = user.id.toString(),
                email = user.email,
                otp = otp,
                name = "${user.firstName} ${user.lastName}",
                emailType = EmailType.VERIFICATION,
            )
        )
    }

    fun verifyEmail(userId: UUID, otp: String) {
        val user = userRepository.findById(userId) ?: throw AppException(UserNotFound)
        if (user.status != UserStatus.PENDING) throw AppException(InvalidToken)
        val savedOtp = getVerifyEmailOtp(user.email) ?: throw AppException(WrongOtp)
        if (!passwordEncoder.matches(otp, savedOtp)) throw AppException(WrongOtp)
        user.previousStatus = user.status
        user.status = UserStatus.ACTIVE
        userRepository.save(user)
        redisStringRepository.deleteValue(getVerifyEmailOtpKey(user.email))
    }

    fun forgotPassword(email: String): LoginResponseDto {
        val user = userRepository.findByEmail(email) ?: throw AppException(UserNotFound)
        if (user.status == UserStatus.BAN) throw AppException(BannedUserRequest)
        if (user.status == UserStatus.PENDING) throw AppException(PendingUserRequest)

        if (isLimitForgotPasswordEmail(user.email)) throw AppException(TOO_MANY_REQUESTS)
        setLimitForgotPasswordEmail(user.email)

        val otp = OtpGenerator.generateOtp()
        saveForgotPasswordOtp(user.email, otp)
        authEventProducer.sendVerifyEmailEvent(
            OtpEmailEvent(
                userId = user.id.toString(),
                email = user.email,
                otp = otp,
                name = "${user.firstName} ${user.lastName}",
                emailType = EmailType.RESET_PASSWORD,
            )
        )

        return LoginResponseDto(
            accessToken = jwtTokenManager.generateToken(
                subject = user.id.toString(),
                tokenType = TokenType.PASSWORD_RESET,
            ),
            refreshToken = null,
            accessTokenType = TokenType.PASSWORD_RESET.name,
            clientId = null,
            userId = user.id.toString()
        )
    }

    fun sendVerifyPasswordOtp(userId: UUID) {
        val user = userRepository.findById(userId) ?: throw AppException(UserNotFound)
        if (isLimitForgotPasswordEmail(
                user.email,
            )
        ) {
            throw AppException(TOO_MANY_REQUESTS)
        }

        setLimitForgotPasswordEmail(
            user.email,
        )

        val otp = OtpGenerator.generateOtp()
        saveForgotPasswordOtp(user.email, otp)

        // Send message
        authEventProducer.sendVerifyEmailEvent(
            OtpEmailEvent(
                userId = user.id.toString(),
                email = user.email,
                otp = otp,
                name = "${user.firstName} ${user.lastName}",
                emailType = EmailType.RESET_PASSWORD,
            )
        )
    }

    fun resetPassword(otp: String, newPassword: String, userId: UUID) {
        val user = userRepository.findById(userId) ?: throw AppException(UserNotFound)
        val oldOtp = getForgotPasswordOtp(user.email) ?: throw AppException(WrongOtp)
        if (!passwordEncoder.matches(otp, oldOtp)) {
            throw AppException(WrongOtp)
        }
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
        redisStringRepository.deleteValue(getForgotPasswordOtpKey(user.email))
    }

    fun twoFactorAuth(userId: UUID, otp: String, ua: String): LoginResponseDto {
        val user = userRepository.findById(userId) ?: throw AppException(UserNotFound)
        val secret = user.totpSecret ?: throw AppException(WrongOtp)

        if (!totpService.verifyCode(secret, otp)) {
            throw AppException(WrongOtp)
        }

        return genLoginResponse(user.id!!, ua)
    }

    fun logoutDevices(userId: UUID, devicesId: List<UUID>) {
        val user = userRepository.findById(userId) ?: throw AppException(UserNotFound)
        val tokens = tokenRepository.findTokenByUserId(user.id!!)
        tokens.forEach { token ->
            if (devicesId.contains(token.id)) tokenRepository.delete(token)
        }
    }

    private fun getForgotPasswordOtp(email: String): String? {
        return redisStringRepository.getValue(
            getForgotPasswordOtpKey(email)
        )
    }

    private fun saveForgotPasswordOtp(email: String, otp: String) {
        return redisStringRepository.saveValue(
            getForgotPasswordOtpKey(email),
            passwordEncoder.encode(otp),
            Duration.ofMinutes(15)
        )
    }

    private fun getForgotPasswordOtpKey(email: String): String {
        return "$PREFIX_FORGOT_PASSWORD_OTP:$email"
    }

    private fun setLimitForgotPasswordEmail(email: String) {
        redisStringRepository.saveValue(
            getLimitForgotPasswordEmailKey(email),
            "1",
            Duration.ofMinutes(1)
        )
    }

    private fun isLimitForgotPasswordEmail(email: String): Boolean {
        return redisStringRepository.getValue(
            getLimitForgotPasswordEmailKey(email)
        ) != null
    }

    private fun getLimitForgotPasswordEmailKey(email: String): String {
        return "$PREFIX_LIMIT_FORGOT_PASSWORD:$email"
    }

    private fun genLoginResponse(userId: UUID, ua: String): LoginResponseDto {
        val rfToken = jwtTokenManager.generateToken(
            subject = userId.toString(),
            tokenType = TokenType.REFRESH,
            expiresIn = TokenType.REFRESH.defaultExpire
        )
        val refreshToken = tokenRepository.save(
            Token(
                userId,
                sha256(rfToken),
                ua
            )
        )

        // save refreshToken
        return LoginResponseDto(
            accessToken = jwtTokenManager.generateToken(
                subject = userId.toString(),
                tokenType = TokenType.ACCESS,
            ),
            refreshToken = rfToken,
            accessTokenType = TokenType.ACCESS.name,
            clientId = refreshToken.id.toString(),
            userId = userId.toString()
        )
    }

    private fun getVerifyEmailOtp(email: String): String? {
        return redisStringRepository.getValue(
            getVerifyEmailOtpKey(
                email
            )
        )
    }

    private fun saveVerifyEmailOtp(email: String, otp: String) {
        redisStringRepository.saveValue(
            getVerifyEmailOtpKey(email),
            passwordEncoder.encode(otp),
            Duration.ofMinutes(15)
        )
    }

    private fun getVerifyEmailOtpKey(email: String): String {
        return "$PREFIX_VERIFY_EMAIL_TOKEN:${email}"
    }

    private fun calcFailedPasswordAttemptCount(email: String): Int {
        val countString = redisStringRepository.getValue(getFailedPasswordAttemptCountKey(email))
        return countString?.toIntOrNull() ?: 0
    }

    private fun saveFailedPasswordAttemptCount(email: String, count: Int) {
        redisStringRepository.saveValue(
            getFailedPasswordAttemptCountKey(email),
            count.toString(),
            Duration.ofMinutes(15)
        )
    }

    private fun getFailedPasswordAttemptCountKey(email: String): String {
        return "$PREFIX_FAILED_PASSWORD_ATTEMPT:${email}"
    }



    private fun getLimitEmailValidationKey(email: String): String {
        return "$PREFIX_LIMIT_VERIFY_EMAIL:${email}"
    }

    private fun isLimitEmailValidation(email: String): Boolean {
        return redisStringRepository.getValue(getLimitEmailValidationKey(email)) != null
    }

    private fun setLimitEmailValidation(email: String) {
        redisStringRepository.saveValue(
            getLimitEmailValidationKey(email),
            "1",
            Duration.ofMinutes(1)
        )
    }

}