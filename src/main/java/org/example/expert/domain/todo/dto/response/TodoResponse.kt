package org.example.expert.domain.todo.dto.response

import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.user.dto.response.UserResponse
import java.time.LocalDateTime


data class TodoResponse(
    val id: Long?,
    val title: String?,
    val contents: String?,
    val weather: String?,
    val user: UserResponse,
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?,
) {
    companion object {
        fun from(todo: Todo) = TodoResponse(
            id = todo.id,
            title = todo.title,
            contents = todo.contents,
            weather = todo.weather,
            user = UserResponse(todo.user.id, todo.user.email),
            createdAt = todo.createdAt,
            modifiedAt = todo.modifiedAt
        )
    }
}
