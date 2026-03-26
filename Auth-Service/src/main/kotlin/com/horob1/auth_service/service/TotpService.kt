package com.horob1.auth_service.service

import com.warrenstrange.googleauth.GoogleAuthenticator
import com.warrenstrange.googleauth.GoogleAuthenticatorKey
import org.springframework.stereotype.Service
import java.net.URLEncoder

@Service
class TotpService {
    private val googleAuthenticator = GoogleAuthenticator()

    companion object {
        const val ISSUER = "Docube"
    }

    /**
     * Generate a new TOTP secret key (Base32 encoded)
     */
    fun generateSecret(): String {
        val key: GoogleAuthenticatorKey = googleAuthenticator.createCredentials()
        return key.key
    }

    /**
     * Generate otpauth:// URL for QR code scanning
     * Format: otpauth://totp/{issuer}:{email}?secret={secret}&issuer={issuer}
     */
    fun generateQrCodeUrl(email: String, secret: String): String {
        val encodedEmail = URLEncoder.encode(email, "UTF-8")
        val encodedIssuer = URLEncoder.encode(ISSUER, "UTF-8")
        return "otpauth://totp/$encodedIssuer:$encodedEmail?secret=$secret&issuer=$encodedIssuer"
    }

    /**
     * Verify a TOTP code against the secret
     * Allows for time window variance (±1 window by default)
     */
    fun verifyCode(secret: String, code: String): Boolean {
        println("DEBUG: Verifying TOTP code: $code with secret: $secret")
        return try {
            val codeInt = code.toInt()
            val result = googleAuthenticator.authorize(secret, codeInt)
            println("DEBUG: TOTP verification result: $result")
            result
        } catch (e: NumberFormatException) {
            println("DEBUG: TOTP verification failed due to format error: ${e.message}")
            false
        }
    }
}
