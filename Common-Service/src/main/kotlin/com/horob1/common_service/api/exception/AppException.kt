package com.horob1.common_service.api.exception

class AppException(
    val appError: AppError,
) : RuntimeException()