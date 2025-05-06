package org.example.expert.domain.user.enums

import org.example.expert.domain.common.exception.InvalidRequestException

enum class UserRole {
    ADMIN, USER;

    companion object {
        fun of(role: String): UserRole =
            entries.firstOrNull { r: UserRole -> r.name.equals(role, ignoreCase = true) }
                ?: throw InvalidRequestException("유효하지 않은 UerRole")
    }
}
