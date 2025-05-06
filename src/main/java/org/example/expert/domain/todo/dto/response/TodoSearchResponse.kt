package org.example.expert.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoSearchResponse {
	private Long todoId;

	private String title;

	private Long managerCount;

	private Long commentCount;
}
