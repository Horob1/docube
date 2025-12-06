package com.horob1.auth_service.service.command

import com.horob1.auth_service.api.dto.request.RegisterDto
import com.horob1.auth_service.api.dto.response.LoginResponseDto
import com.horob1.auth_service.api.exception.BannedUserRequest
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
import com.horob1.auth_service.util.jwt.JwtTokenManager
import com.horob1.common_service.api.exception.AppError
import com.horob1.common_service.api.exception.AppException
import com.horob1.common_service.enums.EmailType
import com.horob1.common_service.util.OtpGenerator
import com.horob1.common_service.enums.UserStatus
import com.horob1.common_service.enums.TokenType
import com.horob1.common_service.kafka.event.auth.OtpEmailEvent
import com.horob1.common_service.util.sha256
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
) {
    companion object {
        const val MAX_FAILED_PASSWORD_ATTEMPT = 5
        const val PREFIX_FAILED_PASSWORD_ATTEMPT = "FAILED_PASSWORD_ATTEMPT"
        const val PREFIX_2FA_TOKEN = "2FA_TOKEN"
        const val PREFIX_VERIFY_EMAIL_TOKEN = "VERIFY_EMAIL_TOKEN"
        const val PREFIX_LIMIT_VERIFY_EMAIL = "LIMIT_VERIFY_EMAIL"
        const val PREFIX_LIMIT_FORGOT_PASSWORD = "LIMIT_FORGOT_PASSWORD"
        const val PREFIX_FORGOT_PASSWORD_OTP = "FORGOT_PASSWORD"
        const val PREFIX_LIMIT_2FA_EMAIL = "LIMIT_2FA_EMAIL"
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
                clientId = null
            )
        }

        // Check 2fa
        if (user.is2FAEnabled) {
            // Gen otp and token
            val otp = OtpGenerator.generateOtp()

            // Save to cache
            save2FAOtp(email, otp)

            // Send message
            authEventProducer.sendVerifyEmailEvent(
                OtpEmailEvent(
                    userId = user.id.toString(),
                    email = email,
                    otp = otp,
                    name = "${user.firstName} ${user.lastName}",
                    emailType = EmailType.TWO_FACTOR_AUTH,
                )
            )

            // Gen access token
            return LoginResponseDto(
                accessToken = jwtTokenManager.generateToken(
                    subject = user.id.toString(),
                    tokenType = TokenType.TWO_FA_VERIFY,
                ),
                refreshToken = null,
                accessTokenType = TokenType.TWO_FA_VERIFY.name,
                clientId = null
            )
        }

        // gen token
        return genLoginResponse(user.id!!, ua)
    }

    fun loginGoogle(token: String, ua: String): LoginResponseDto {
        val payload = googleAuthClient.verify(token)

        val user =
            userRepository.findById(UUID.fromString(payload.subject))

        val userId = if (user == null) {
            userRepository.save(
                User(
                    email = payload.email,
                    firstName = "USER",
                    lastName = "GOOGLE",
                    password = passwordEncoder.encode(UUID.randomUUID().toString()),
                    status = UserStatus.ACTIVE,
                )
            ).id

        } else user.id

        if (user?.status == UserStatus.BAN) {
            throw AppException(BannedUserRequest)
        }

        //Gen token
        return genLoginResponse(userId!!, ua)
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
            clientId = oldToken.id.toString()
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
        val savedOtp = getVerifyEmailOtp(user.email) ?: throw AppException(WrongOtp)
        if (
            !passwordEncoder.matches(
                otp,
                savedOtp
            )
        ) throw AppException(WrongOtp)
        // Update status
        user.previousStatus = user.status
        user.status = UserStatus.ACTIVE
        userRepository.save(user)
    }

    fun forgotPassword(email: String): LoginResponseDto {
        val user = userRepository.findByEmail(email) ?: throw AppException(UserNotFound)
        if (user.status == UserStatus.BAN) {
            throw AppException(BannedUserRequest)
        }
        return LoginResponseDto(
            accessToken = jwtTokenManager.generateToken(
                subject = user.id.toString(),
                tokenType = TokenType.PASSWORD_RESET,
            ),
            refreshToken = null,
            accessTokenType = TokenType.PASSWORD_RESET.name,
            clientId = null
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
        // Update password
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
    }

    fun send2FaOtpEmail(userId: UUID) {
        val user = userRepository.findById(userId) ?: throw AppException(UserNotFound)
        if (isLimit2FAEmail(email = user.email)) {
            throw AppException(TOO_MANY_REQUESTS)
        }
        setLimit2FAEmail(email = user.email)

        val otp = OtpGenerator.generateOtp()
        save2FAOtp(user.email, otp)

        // Send message
        authEventProducer.sendVerifyEmailEvent(
            OtpEmailEvent(
                userId = user.id.toString(),
                email = user.email,
                otp = otp,
                name = "${user.firstName} ${user.lastName}",
                emailType = EmailType.TWO_FACTOR_AUTH,
            )
        )
    }

    fun twoFactorAuth(userId: UUID, otp: String, ua: String): LoginResponseDto {
        val user = userRepository.findById(userId) ?: throw AppException(UserNotFound)
        val oldOtp = get2FAOtp(user.email) ?: throw AppException(WrongOtp)
        if (!passwordEncoder.matches(otp, oldOtp)) {
            throw AppException(WrongOtp)
        }

        return genLoginResponse(user.id!!, ua)
    }

    fun logoutDevices(userId: UUID, devicesId: List<UUID>) {
        val tokens = tokenRepository.findTokenByUserId(userId)
        tokens.forEach { token ->
            if (devicesId.contains(token.id)) tokenRepository.delete(token)
        }
    }

    private fun get2FAOtp(email: String): String? {
        return redisStringRepository.getValue(
            get2FAOtpKey(email)
        )
    }

    private fun setLimit2FAEmail(email: String) {
        redisStringRepository.saveValue(
            getLimit2FAEmailKey(email),
            "1",
            Duration.ofMinutes(1)
        )
    }

    private fun isLimit2FAEmail(email: String): Boolean {
        return redisStringRepository.getValue(getLimit2FAEmailKey(email)) != null
    }

    private fun getLimit2FAEmailKey(email: String): String {
        return "${PREFIX_LIMIT_2FA_EMAIL}:${email}"
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
            clientId = refreshToken.id.toString()
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

    private fun save2FAOtp(email: String, otp: String) {
        redisStringRepository.saveValue(get2FAOtpKey(email), passwordEncoder.encode(otp), Duration.ofMinutes(15))
    }

    private fun get2FAOtpKey(email: String): String {
        return "$PREFIX_2FA_TOKEN:${email}"
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