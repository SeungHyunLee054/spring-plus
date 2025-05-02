package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoCustomRepository {
	Optional<Todo> findByIdWithUser(Long todoId);

	Page<TodoSearchResponse> searchTodos(String keyword, String managerNickname, LocalDateTime startDateTime,
		LocalDateTime endDateTime, Pageable pageable);
}
