package org.example.expert.domain.todo.service

import org.example.expert.client.weather.WeatherClient
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.todo.dto.request.TodoSaveRequest
import org.example.expert.domain.todo.dto.response.TodoResponse
import org.example.expert.domain.todo.dto.response.TodoSaveResponse
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.todo.repository.TodoRepository
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class TodoService(private val todoRepository: TodoRepository, private val weatherClient: WeatherClient) {

    @Transactional
    fun saveTodo(authUser: AuthUser, todoSaveRequest: TodoSaveRequest): TodoSaveResponse {
        val user = User.fromAuthUser(authUser)

        val weather = weatherClient.getTodayWeather()

        val newTodo = Todo(
            title = todoSaveRequest.title,
            contents = todoSaveRequest.contents,
            weather = weather,
            user = user
        )
        val savedTodo = todoRepository.save(newTodo)

        return TodoSaveResponse(
            id = savedTodo.id,
            title = savedTodo.title,
            contents = savedTodo.contents,
            weather = weather,
            user = UserResponse(user.id, user.email)
        )
    }

    fun getTodos(
        page: Int, size: Int, weather: String?, modifiedFrom: LocalDate?,
        modifiedTo: LocalDate?,
    ): Page<TodoResponse> {
        val (startDateTime, endDateTime) = Pair(modifiedFrom, modifiedTo)
            .validateDateRange("수정일 검색은 시작일과 종료일을 모두 입력해야 합니다.")

        val pageable: Pageable = PageRequest.of(page - 1, size)

        val todos = todoRepository.findTodosByWeatherAndDateRange(
            weather = weather,
            modifiedFrom = startDateTime,
            modifiedTo = endDateTime,
            pageable = pageable
        )

        return todos.map(TodoResponse::from)
    }

    fun getTodo(todoId: Long): TodoResponse {
        val todo = todoRepository.findByIdWithUser(todoId)
            ?: throw InvalidRequestException("Todo not found")

        return todo.let(TodoResponse::from)
    }

    fun searchTodos(
        keyword: String?, managerNickname: String?, createdFrom: LocalDate?, createdTo: LocalDate?,
        pageable: Pageable,
    ): Page<TodoSearchResponse> {
        val (startDateTime, endDateTime) = Pair(createdFrom, createdTo)
            .validateDateRange("생성일 검색은 시작일과 종료일을 모두 입력해야 합니다.")

        return todoRepository.searchTodos(
            keyword = keyword,
            managerNickname = managerNickname,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            pageable = pageable
        )
    }

    private fun Pair<LocalDate?, LocalDate?>.validateDateRange(errorMessage: String):
            Pair<LocalDateTime?, LocalDateTime?> {
        if ((first == null).xor(second == null)) {
            throw InvalidRequestException(errorMessage)
        }

        return Pair(first?.atStartOfDay(), second?.atTime(23, 59, 59))
    }
}
