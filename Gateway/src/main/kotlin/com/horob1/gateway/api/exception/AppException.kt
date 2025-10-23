package com.horob1.gateway.api.exception

class AppException(
    val appError: AppError,
) : RuntimeException()