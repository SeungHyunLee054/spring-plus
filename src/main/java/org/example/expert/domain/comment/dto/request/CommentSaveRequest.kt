package org.example.expert.domain.comment.dto.request

import jakarta.validation.constraints.NotBlank

data class CommentSaveRequest(
    @NotBlank
    val contents: String
)
