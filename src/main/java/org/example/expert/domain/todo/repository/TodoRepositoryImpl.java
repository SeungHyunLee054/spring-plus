package org.example.expert.domain.todo.repository;

import java.util.Optional;

import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<Todo> findByIdWithUser(Long todoId) {
		QTodo todo = QTodo.todo;

		return Optional.ofNullable(jpaQueryFactory.select(todo)
			.from(todo)
			.leftJoin(todo.user)
			.fetchJoin()
			.where(todo.id.eq(todoId))
			.fetchOne());
	}
}
