package org.example.expert.domain.todo.dto.request

import jakarta.validation.constraints.NotBlank

data class TodoSaveRequest(
    @NotBlank
    val title: String,

    @NotBlank
    val contents: String,
)
