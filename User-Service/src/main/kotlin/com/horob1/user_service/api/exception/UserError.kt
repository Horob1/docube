package com.horob1.user_service.api.exception


import com.horob1.common_service.api.exception.AppError
import org.springframework.http.HttpStatus

val UserExisted = AppError.CustomError(
    status = HttpStatus.CONFLICT,
    code = "USER_0001",
    message = "User already exists",
)

val UserNotFound = AppError.CustomError(
    status = HttpStatus.NOT_FOUND,
    code = "USER_0002",
    message = "User not found",
)

val UserIdMissing = AppError.CustomError(
    status = HttpStatus.BAD_REQUEST,
    code = "USER_0003",
    message = "User id missing",
)

val UserIsActive = AppError.CustomError(
    status = HttpStatus.FORBIDDEN,
    code = "USER_0004",
    message = "User is active",
)

val PasswordNotMatch = AppError.CustomError(
    status = HttpStatus.FORBIDDEN,
    code = "USER_0005",
    message = "Password does not match",
)

val PasswordIsSameAsOld = AppError.CustomError(
    status = HttpStatus.CONFLICT,
    code = "USER_0006",
    message = "Password does not match",
)