package com.horob1.auth_service.util.jwt

import com.horob1.common_service.api.exception.AppError
import com.horob1.common_service.api.exception.AppException
import com.horob1.common_service.enums.TokenType
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class JwtTokenManager(
    @param:Value("\${JWT_ACCESS_SECRET}") private val accessSecret: String,
    @param:Value("\${JWT_REFRESH_SECRET}") private val refreshSecret: String,
    @param:Value("\${JWT_EMAIL_SECRET}") private val emailSecret: String,
    @param:Value("\${JWT_RESET_SECRET}") private val resetSecret: String,
    @param:Value("\${JWT_2FA_SECRET}") private val twoFASecret: String,
) {

    private val secretKeys = mapOf(
        TokenType.ACCESS to Keys.hmacShaKeyFor(accessSecret.toByteArray()),
        TokenType.REFRESH to Keys.hmacShaKeyFor(refreshSecret.toByteArray()),
        TokenType.EMAIL_VERIFY to Keys.hmacShaKeyFor(emailSecret.toByteArray()),
        TokenType.PASSWORD_RESET to Keys.hmacShaKeyFor(resetSecret.toByteArray()),
        TokenType.TWO_FA_VERIFY to Keys.hmacShaKeyFor(twoFASecret.toByteArray())
    )

    /**
     * Sinh JWT token
     * @param subject - thường là userId hoặc email
     * @param claims - thêm thông tin bổ sung
     * @param tokenType
     * @param expiresIn
     */
    fun generateToken(
        subject: String,
        claims: Map<String, Any> = emptyMap(),
        tokenType: TokenType,
        expiresIn: Duration = Duration.ofMinutes(15),
    ): String {
        val now = Date()
        val expiry = Date(now.time + expiresIn.toMillis())

        val key = secretKeys[tokenType]
            ?: throw AppException(AppError.InternalServerError)

        return Jwts.builder()
            .subject(subject)
            .claims(claims)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    /**
     * Kiểm tra & parse token, trả về claims nếu hợp lệ
     */
    fun validateAndParse(token: String, tokenType: TokenType): Claims {
        val key = secretKeys[tokenType]
            ?: throw AppException(AppError.InternalServerError)

        val jwt = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)

        return jwt.payload
    }
}
