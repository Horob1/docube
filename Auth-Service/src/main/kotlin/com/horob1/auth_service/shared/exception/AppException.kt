package com.horob1.auth_service.shared.exception

class AppException(
    val appError: AppError,
) : RuntimeException()