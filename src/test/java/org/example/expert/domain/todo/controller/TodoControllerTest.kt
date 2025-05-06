package org.example.expert.domain.todo.controller

import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.todo.dto.response.TodoResponse
import org.example.expert.domain.todo.service.TodoService
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.entity.User
import org.example.expert.domain.user.enums.UserRole
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDateTime

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TodoController::class)
class TodoControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var todoService: TodoService

    @Test
    @DisplayName("todo 단건 조회에 성공한다")
    fun success_getTodo() {
        // given
        val todoId = 1L
        val title = "title"
        val authUser = AuthUser(1L, "email", UserRole.USER)
        val user: User = User.fromAuthUser(authUser)
        val userResponse = UserResponse(user.id, user.email)
        val response = TodoResponse(
            id = todoId,
            title = title,
            contents = "contents",
            weather = "Sunny",
            user = userResponse,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )

        // when
        Mockito.`when`(todoService.getTodo(todoId)).thenReturn(response)

        // then
        mockMvc.get("/todos/$todoId")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(todoId) }
                jsonPath("$.title") { value(title) }
            }
    }

    @Test
    @DisplayName("todo 단건 조회 시 todo가 존재하지 않아 예외가 발생한다")
    fun fail_getTodo_todoNotFound() {
        // given
        val todoId = 1L

        // when
        Mockito.`when`(todoService.getTodo(todoId))
            .thenThrow(InvalidRequestException("Todo not found"))

        // then
        mockMvc.get("/todos/$todoId")
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(HttpStatus.BAD_REQUEST.name) }
                jsonPath("$.code") { value(HttpStatus.BAD_REQUEST.value()) }
                jsonPath("$.message") { value("Todo not found") }
            }
    }
}
