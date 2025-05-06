package org.example.expert.domain.common.dto

import org.example.expert.domain.user.enums.UserRole

data class AuthUser(
    val id: Long,
    val email: String,
    val userRole: UserRole,
)
