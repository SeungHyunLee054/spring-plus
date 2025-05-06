package org.example.expert.domain.user.service

import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest
import org.example.expert.domain.user.enums.UserRole
import org.example.expert.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAdminService(private val userRepository: UserRepository) {
    @Transactional
    fun changeUserRole(userId: Long, userRoleChangeRequest: UserRoleChangeRequest) {
        val user = userRepository.getUserById(userId) ?: throw InvalidRequestException("User not found")

        user.updateRole(UserRole.of(userRoleChangeRequest.role))
    }
}
