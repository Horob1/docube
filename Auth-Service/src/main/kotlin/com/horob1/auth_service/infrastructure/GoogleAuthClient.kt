package com.horob1.auth_service.infrastructure

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.horob1.auth_service.config.GoogleProperties
import com.horob1.common_service.api.exception.AppError
import com.horob1.common_service.api.exception.AppException
import org.springframework.stereotype.Component

@Component
class GoogleAuthClient(
    private val googleProperties: GoogleProperties
) {
    private val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance())
        .setAudience(listOf(googleProperties.clientId))
        .build()

    fun verify(idTokenString: String): GoogleIdToken.Payload =
        runCatching { verifier.verify(idTokenString) }
            .getOrNull()
            ?.payload
            ?: throw AppException(AppError.AuthenticationFailed)
}