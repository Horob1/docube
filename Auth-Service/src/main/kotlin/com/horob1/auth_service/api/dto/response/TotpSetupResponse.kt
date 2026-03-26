package com.horob1.auth_service.api.dto.response

data class TotpSetupResponse(
    val secret: String,
    val qrCodeUrl: String
)
