package com.horob1.auth_service.service.query

import com.horob1.auth_service.api.exception.PermissionNotFound
import com.horob1.auth_service.domain.model.permission.Permission
import com.horob1.auth_service.domain.repository.PermissionRepository
import com.horob1.common_service.api.exception.AppException
import org.springframework.stereotype.Service
import java.util.*

@Service
class PermissionQueryHandler(
    private val permissionRepository: PermissionRepository,
) {
    fun getByID(permissionID: UUID): Permission {
        return permissionRepository.findPermissionById(permissionID) ?: throw AppException(PermissionNotFound)
    }

    fun getAll(): List<Permission> {
        return permissionRepository.findAll()
    }
}