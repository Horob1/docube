package com.horob1.auth_service.api.exception


import com.horob1.auth_service.shared.exception.AppError
import org.springframework.http.HttpStatus

val UserExisted = AppError.CustomError(
    status = HttpStatus.CONFLICT,
    code = "USER_0001",
    message = "User already exists",
)

val UserIdMissing = AppError.CustomError(
    status = HttpStatus.BAD_REQUEST,
    code = "USER_0002",
    message = "User id missing",
)

val UserIsActive = AppError.CustomError(
    status = HttpStatus.FORBIDDEN,
    code = "USER_0003",
    message = "User is active",
)

val PasswordNotMatch = AppError.CustomError(
    status = HttpStatus.FORBIDDEN,
    code = "USER_0004",
    message = "Password does not match",
)

val PasswordIsSameAsOld = AppError.CustomError(
    status = HttpStatus.CONFLICT,
    code = "USER_0005",
    message = "Password does not match",
)