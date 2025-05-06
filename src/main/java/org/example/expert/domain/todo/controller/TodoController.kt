package org.example.expert.domain.todo.controller

import jakarta.validation.Valid
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.todo.dto.request.TodoSaveRequest
import org.example.expert.domain.todo.dto.response.TodoResponse
import org.example.expert.domain.todo.dto.response.TodoSaveResponse
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.service.TodoService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
class TodoController(private val todoService: TodoService) {

    @PostMapping("/todos")
    fun saveTodo(
        @AuthenticationPrincipal authUser: AuthUser,
        @RequestBody @Valid todoSaveRequest: TodoSaveRequest,
    ): ResponseEntity<TodoSaveResponse> =
        ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest))

    @GetMapping("/todos")
    fun getTodos(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) weather: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) modifiedFrom: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) modifiedTo: LocalDate?,
    ): ResponseEntity<Page<TodoResponse>> =
        ResponseEntity.ok(
            todoService.getTodos(
                page,
                size,
                weather,
                modifiedFrom,
                modifiedTo
            )
        )

    @GetMapping("/todos/{todoId}")
    fun getTodo(@PathVariable todoId: Long): ResponseEntity<TodoResponse> =
        ResponseEntity.ok(todoService.getTodo(todoId))

    @GetMapping("/todos/search")
    fun searchTodos(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) managerNickname: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) createdFrom: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) createdTo: LocalDate?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<Page<TodoSearchResponse>> =
        ResponseEntity.ok(
            todoService.searchTodos(
                keyword, managerNickname, createdFrom, createdTo,
                PageRequest.of(page - 1, size)
            )
        )
}
