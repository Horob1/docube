package com.horob1.auth_service.api.exception

import com.horob1.common_service.api.exception.AppError
import org.springframework.http.HttpStatus

val ExistedPermission = AppError.CustomError(
    status = HttpStatus.CONFLICT,
    message = "Permission existed",
    code = "PERMIT_0001"
)

val PermissionNotFound = AppError.CustomError(
    status = HttpStatus.NOT_FOUND,
    message = "Permission not found",
    code = "PERMIT_0002"
)

val NewPermissionNameExist = AppError.CustomError(
    status = HttpStatus.CONFLICT,
    message = "New Permission existed",
    code = "PERMIT_0003"
)