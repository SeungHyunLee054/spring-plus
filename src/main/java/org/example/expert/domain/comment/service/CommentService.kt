package org.example.expert.domain.comment.service

import org.example.expert.domain.comment.dto.request.CommentSaveRequest
import org.example.expert.domain.comment.dto.response.CommentResponse
import org.example.expert.domain.comment.dto.response.CommentSaveResponse
import org.example.expert.domain.comment.entity.Comment
import org.example.expert.domain.comment.repository.CommentRepository
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.todo.repository.TodoRepository
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService(private val todoRepository: TodoRepository, private val commentRepository: CommentRepository) {

    @Transactional
    fun saveComment(authUser: AuthUser, todoId: Long, commentSaveRequest: CommentSaveRequest): CommentSaveResponse {
        val user = User.fromAuthUser(authUser)
        val todo = todoRepository.getTodoById(todoId)
            ?: throw InvalidRequestException("Todo not found")

        val newComment = Comment(
            contents = commentSaveRequest.contents,
            user = user,
            todo = todo
        )

        val savedComment = commentRepository.save(newComment)

        return CommentSaveResponse(
            id = savedComment.id,
            contents = savedComment.contents,
            user = UserResponse(user.id, user.email)
        )
    }

    fun getComments(todoId: Long): List<CommentResponse> =
        commentRepository.findWithUserByTodoId(todoId)
            .map { comment ->
                CommentResponse(
                    id = comment.id,
                    contents = comment.contents,
                    user = UserResponse(
                        id = comment.user.id,
                        email = comment.user.email
                    )
                )
            }
}
