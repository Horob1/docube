package com.horob1.auth_service.application.query

import com.horob1.auth_service.domain.repository.PermissionRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PermissionQueryHandler(
    private val permissionRepository: PermissionRepository,
) {
    fun getByID(permissionID: UUID) = permissionRepository.getPermissionById(permissionID)

    fun getAll() = permissionRepository.getAll()
}