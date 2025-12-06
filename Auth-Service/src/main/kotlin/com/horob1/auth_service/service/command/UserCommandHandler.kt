package com.horob1.auth_service.service.command

import com.horob1.auth_service.api.dto.request.CreateUserDto
import com.horob1.auth_service.api.dto.request.UpdateUserDto
import com.horob1.auth_service.api.exception.UserExisted
import com.horob1.auth_service.api.exception.UserIsActive
import com.horob1.auth_service.api.exception.UserNotFound
import com.horob1.auth_service.domain.model.user.User
import com.horob1.auth_service.domain.model.user.UserSummary
import com.horob1.auth_service.domain.model.user.toSummary
import com.horob1.auth_service.domain.repository.RoleRepository
import com.horob1.auth_service.domain.repository.UserRepository
import com.horob1.common_service.api.exception.AppException
import com.horob1.common_service.enums.UserStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserCommandHandler(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun create(createData: CreateUserDto): UserSummary {
        if (userRepository.isExists(createData.email)) {
            throw AppException(UserExisted)
        }

        return userRepository.save(
            User(
                email = createData.email,
                firstName = createData.firstName,
                lastName = createData.lastName,
                password = passwordEncoder.encode(createData.password),
                status = createData.status,
                phoneNumber = createData.phoneNumber,
                address = createData.address,
            )
        ).toSummary()
    }

    fun update(id: UUID, updateData: UpdateUserDto): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        user.firstName = updateData.firstName
        user.lastName = updateData.lastName
        user.phoneNumber = updateData.phoneNumber
        user.address = updateData.address
        user.is2FAEnabled = updateData.is2FAEnabled

        return userRepository.save(user).toSummary()
    }

    fun banUser(id: UUID) {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        user.previousStatus = user.status
        user.status = UserStatus.BAN
        userRepository.save(user).toSummary()
    }

    fun unbanUser(id: UUID) {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        if (user.status != UserStatus.BAN) {
            throw AppException(UserIsActive)
        }
        user.status = user.previousStatus!!
        user.previousStatus = UserStatus.BAN
        userRepository.save(user).toSummary()
    }

    fun updateRole(id: UUID, roleIds: List<UUID>) {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        val roles = roleRepository.findRolesByIdList(roleIds)
        user.roles = roles.toMutableSet()
        userRepository.save(
            user
        )
    }
}