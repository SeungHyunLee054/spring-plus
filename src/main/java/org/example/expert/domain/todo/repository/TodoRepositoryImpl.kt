package org.example.expert.domain.todo.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.expert.domain.comment.entity.QComment
import org.example.expert.domain.manager.entity.QManager
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.entity.QTodo
import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.user.entity.QUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TodoRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : TodoCustomRepository {

    override fun findByIdWithUser(todoId: Long): Todo? {
        val todo = QTodo.todo

        return jpaQueryFactory.selectFrom(todo)
            .leftJoin(todo.user)
            .fetchJoin()
            .where(todo.id.eq(todoId))
            .fetchOne()
    }

    override fun findTodosByWeatherAndDateRange(
        weather: String?,
        modifiedFrom: LocalDateTime?,
        modifiedTo: LocalDateTime?,
        pageable: Pageable,
    ): Page<Todo> {
        val todo = QTodo.todo

        val builder = BooleanBuilder().apply {
            if (weather != null) {
                and(todo.weather.eq(weather))
            }
            if (modifiedFrom != null && modifiedTo != null) {
                and(todo.modifiedAt.between(modifiedFrom, modifiedTo))
            }
        }

        val content = jpaQueryFactory
            .selectFrom(todo)
            .where(builder)
            .orderBy(todo.modifiedAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val totalCount = jpaQueryFactory
            .select(todo.id.count())
            .from(todo)
            .where(builder)
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, totalCount)
    }

    override fun searchTodos(
        keyword: String?,
        managerNickname: String?,
        startDateTime: LocalDateTime?,
        endDateTime: LocalDateTime?,
        pageable: Pageable,
    ): Page<TodoSearchResponse> {
        val todo = QTodo.todo
        val manager = QManager.manager
        val comment = QComment.comment
        val user = QUser.user

        val builder = BooleanBuilder().apply {
            if (!keyword.isNullOrBlank()) {
                and(todo.title.containsIgnoreCase(keyword))
            }

            if (!managerNickname.isNullOrBlank()) {
                and(manager.user.nickname.containsIgnoreCase(managerNickname))
            }

            if (startDateTime != null && endDateTime != null) {
                and(todo.createdAt.between(startDateTime, endDateTime))
            }
        }

        val content = jpaQueryFactory
            .select(
                Projections.constructor(
                    TodoSearchResponse::class.java,
                    todo.id,
                    todo.title,
                    manager.count(),
                    comment.count()
                )
            )
            .from(todo)
            .leftJoin(manager).on(manager.todo.eq(todo))
            .leftJoin(comment).on(comment.todo.eq(todo))
            .leftJoin(manager.user, user)
            .where(builder)
            .groupBy(todo.id)
            .orderBy(todo.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val totalCount = jpaQueryFactory
            .select(todo.id.count())
            .from(todo)
            .where(builder)
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, totalCount)
    }
}
