package com.horob1.auth_service.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.horob1.auth_service.domain.model.permission.Permission
import com.horob1.auth_service.domain.model.role.Role
import com.horob1.auth_service.domain.model.user.User
import com.horob1.auth_service.domain.repository.PermissionRepository
import com.horob1.auth_service.domain.repository.RoleRepository
import com.horob1.auth_service.domain.repository.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.annotation.Transactional

@Configuration
class DataInitializer(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
    private val objectMapper: ObjectMapper
) {

    @Transactional
    @Bean
    fun initializer(): ApplicationRunner = ApplicationRunner {
        // Nếu cả 3 bảng đều trống thì mới seed dữ liệu
        println("⚙️ Initializing default data...")

        if (
            permissionRepository.count() == 0L
        ) {
            // --- Load permissions ---
            val permResource = ClassPathResource("data/permissions.json")
            val permissions: List<Permission> = permResource.inputStream.use { input ->
                objectMapper.readValue(input, object : TypeReference<List<Permission>>() {})
            }
            permissionRepository.saveAll(permissions)
        }

        if (roleRepository.count() == 0L
        ) {
            val managedPermissions = permissionRepository.findAll()

            // --- Load role ---
            val roleResource = ClassPathResource("data/super_role.json")
            val baseRole: Role = roleResource.inputStream.use { input ->
                objectMapper.readValue(input, Role::class.java)
            }
            // Gán thêm permissions
            baseRole.permissions = managedPermissions.toMutableSet()
            roleRepository.save(baseRole)
        }

        if (userRepository.count() == 0L) {
            val savedRoles = roleRepository.findAll()

            // --- Load user ---
            val userResource = ClassPathResource("data/super_user.json")
            val baseUser: User = userResource.inputStream.use { input ->
                objectMapper.readValue(input, User::class.java)
            }
            // Gán role cho user
            baseUser.roles = savedRoles.toMutableSet()
            userRepository.save(baseUser)
        }

        println("✅ Initialized successfully!")
    }
}
