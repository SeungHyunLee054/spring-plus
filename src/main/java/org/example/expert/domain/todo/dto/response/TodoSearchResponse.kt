package org.example.expert.domain.todo.dto.response

data class TodoSearchResponse(
    val todoId: Long?,

    val title: String?,

    val managerCount: Long = 0,

    val commentCount: Long = 0,
)
