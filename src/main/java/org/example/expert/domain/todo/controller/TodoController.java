package org.example.expert.domain.todo.controller;

import java.time.LocalDate;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TodoController {

	private final TodoService todoService;

	@PostMapping("/todos")
	public ResponseEntity<TodoSaveResponse> saveTodo(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody TodoSaveRequest todoSaveRequest
	) {
		return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
	}

	@GetMapping("/todos")
	public ResponseEntity<Page<TodoResponse>> getTodos(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String weather,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate modifiedFrom,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate modifiedTo
	) {
		return ResponseEntity.ok(todoService.getTodos(page, size, weather, modifiedFrom, modifiedTo));
	}

	@GetMapping("/todos/{todoId}")
	public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
		return ResponseEntity.ok(todoService.getTodo(todoId));
	}

	@GetMapping("/todos/search")
	public ResponseEntity<Page<TodoSearchResponse>> searchTodos(
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) String managerNickname,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdFrom,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdTo,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return ResponseEntity.ok(todoService.searchTodos(keyword, managerNickname, createdFrom, createdTo,
			PageRequest.of(page - 1, size)));
	}
}
