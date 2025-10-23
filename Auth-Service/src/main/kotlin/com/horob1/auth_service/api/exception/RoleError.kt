package com.horob1.auth_service.api.exception

import com.horob1.common_service.api.exception.AppError
import org.springframework.http.HttpStatus

val ExistedRole = AppError.CustomError(
    status = HttpStatus.CONFLICT,
    message = "Role existed",
    code = "ROLE_0001"
)

val RoleNotFound = AppError.CustomError(
    status = HttpStatus.NOT_FOUND,
    message = "Role not found",
    code = "ROLE_0002"
)

val NewRoleNameExist = AppError.CustomError(
    status = HttpStatus.CONFLICT,
    message = "New role existed",
    code = "ROLE_0003"
)