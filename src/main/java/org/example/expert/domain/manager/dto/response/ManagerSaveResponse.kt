package org.example.expert.domain.manager.dto.response

import org.example.expert.domain.user.dto.response.UserResponse

data class ManagerSaveResponse(
    val id: Long?,
    val user: UserResponse,
)
