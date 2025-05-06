package org.example.expert.domain.todo.repository

import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.entity.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface TodoCustomRepository {
    fun findByIdWithUser(todoId: Long): Todo?

    fun findTodosByWeatherAndDateRange(
        weather: String?,
        modifiedFrom: LocalDateTime?,
        modifiedTo: LocalDateTime?,
        pageable: Pageable,
    ): Page<Todo>

    fun searchTodos(
        keyword: String?, managerNickname: String?, startDateTime: LocalDateTime?,
        endDateTime: LocalDateTime?, pageable: Pageable,
    ): Page<TodoSearchResponse>
}
