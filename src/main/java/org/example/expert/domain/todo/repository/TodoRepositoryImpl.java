package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
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

	@Override
	public Page<TodoSearchResponse> searchTodos(String keyword, String managerNickname, LocalDateTime startDateTime,
		LocalDateTime endDateTime, Pageable pageable) {
		QTodo todo = QTodo.todo;
		QManager manager = QManager.manager;
		QComment comment = QComment.comment;
		QUser user = QUser.user;

		BooleanBuilder builder = new BooleanBuilder();

		if (keyword != null && !keyword.isBlank()) {
			builder.and(todo.title.containsIgnoreCase(keyword));
		}

		if (managerNickname != null && !managerNickname.isBlank()) {
			builder.and(manager.user.nickname.containsIgnoreCase(managerNickname));
		}

		if (startDateTime != null && endDateTime != null) {
			builder.and(todo.createdAt.between(startDateTime, endDateTime));
		}

		List<TodoSearchResponse> content = jpaQueryFactory
			.select(Projections.constructor(TodoSearchResponse.class,
				todo.id,
				todo.title,
				manager.count(),
				comment.count()))
			.from(todo)
			.leftJoin(manager).on(manager.todo.eq(todo))
			.leftJoin(comment).on(comment.todo.eq(todo))
			.leftJoin(manager.user, user)
			.where(builder)
			.groupBy(todo.id)
			.orderBy(todo.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long totalCount = Optional.ofNullable(jpaQueryFactory
				.select(Wildcard.count)
				.from(todo)
				.where(builder)
				.fetchOne())
			.orElse(0L);

		return new PageImpl<>(content, pageable, totalCount);
	}

}
