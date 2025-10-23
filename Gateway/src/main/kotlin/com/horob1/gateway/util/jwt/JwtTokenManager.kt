package com.horob1.gateway.util.jwt

import com.horob1.gateway.api.exception.AppError
import com.horob1.gateway.enum.TokenType
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import com.horob1.gateway.api.exception.AppException

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

    fun validateAndParse(token: String, tokenType: TokenType): Claims {
        val key = secretKeys[tokenType]
            ?: throw AppException(AppError.InternalServerError)

        try {
            val jwt = Jwts.parser() .verifyWith(key) .build() .parseSignedClaims(token)
            return jwt.payload
        } catch (ex: ExpiredJwtException) {
            throw AppException(AppError.TokenExpired)
        } catch (ex: SignatureException) {
            throw AppException(AppError.InvalidSignature)
        } catch (ex: MalformedJwtException) {
            throw AppException(AppError.InvalidToken)
        } catch (ex: UnsupportedJwtException) {
            throw AppException(AppError.InvalidToken)
        } catch (ex: IllegalArgumentException) {
            throw AppException(AppError.InvalidToken)
        } catch (ex: JwtException) {
            throw AppException(AppError.AuthenticationFailed)
        } catch (ex: Exception) {
            throw AppException(AppError.InternalServerError)
        }
    }
}
