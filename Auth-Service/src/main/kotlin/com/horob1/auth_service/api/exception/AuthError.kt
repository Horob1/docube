package com.horob1.auth_service.api.exception

import com.horob1.common_service.api.exception.AppError
import org.springframework.http.HttpStatus

val InvalidIdentityInfo = AppError.CustomError(
    code = "AUTHS_0001",
    message = "Invalid email or password",
    status = HttpStatus.BAD_REQUEST
)

val BannedUserRequest = AppError.CustomError(
    code = "AUTHS_0002",
    message = "Banned user request",
    status = HttpStatus.FORBIDDEN
)

val FailedLoginLimit = AppError.CustomError(
    code = "AUTHS_0003",
    message = "Failed login count has exceeded the limit. Please try again later.",
    status = HttpStatus.TOO_MANY_REQUESTS
)

val UserExistedRequest = AppError.CustomError(
    code = "AUTHS_0004",
    message = "User exist.",
    status = HttpStatus.CONFLICT
)

val InvalidToken = AppError.CustomError(
    code = "AUTHS_0005",
    message = "Invalid token",
    status = HttpStatus.BAD_REQUEST
)

val UserNotFound = AppError.CustomError(
    code = "AUTHS_0006",
    message = "User not found",
    status = HttpStatus.NOT_FOUND
)

val TOO_MANY_REQUESTS = AppError.CustomError(
    code = "AUTHS_0007",
    message = "Too many requests",
    status = HttpStatus.BAD_REQUEST
)

val WrongOtp = AppError.CustomError(
    code = "AUTHS_0008",
    message = "Wrong otp",
    status = HttpStatus.BAD_REQUEST
)