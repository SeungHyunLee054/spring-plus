package org.example.expert.domain.comment.repository

import org.example.expert.domain.comment.entity.Comment
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = ["user"])
    fun findWithUserByTodoId(todoId: Long): List<Comment>
}
