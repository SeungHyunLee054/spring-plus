package org.example.expert.domain.manager.repository

import org.example.expert.domain.manager.entity.Manager
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface ManagerRepository : JpaRepository<Manager, Long> {
    @EntityGraph(attributePaths = ["user"])
    fun findWithUserByTodoId(todoId: Long?): List<Manager>

    @EntityGraph(attributePaths = ["todo"])
    fun findWithTodoById(id: Long): Manager?
}
