package org.example.expert.domain.comment.dto.response

import org.example.expert.domain.user.dto.response.UserResponse

data class CommentResponse(
    val id: Long?,
    val contents: String?,
    val user: UserResponse
)
