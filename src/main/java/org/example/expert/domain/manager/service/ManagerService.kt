package org.example.expert.domain.manager.service

import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.log.service.LogService
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest
import org.example.expert.domain.manager.dto.response.ManagerResponse
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse
import org.example.expert.domain.manager.entity.Manager
import org.example.expert.domain.manager.repository.ManagerRepository
import org.example.expert.domain.todo.repository.TodoRepository
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.entity.User
import org.example.expert.domain.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ManagerService(
    private val managerRepository: ManagerRepository,
    private val userRepository: UserRepository,
    private val todoRepository: TodoRepository,
    private val logService: LogService,
) {
    private val log = LoggerFactory.getLogger(ManagerService::class.java)

    @Transactional
    fun saveManager(authUser: AuthUser, todoId: Long, managerSaveRequest: ManagerSaveRequest): ManagerSaveResponse {
        // 일정을 만든 유저
        val user = User.fromAuthUser(authUser)
        val todo = todoRepository.findByIdWithUser(todoId) ?: throw InvalidRequestException("Todo not found")

        if (user.id != todo.user.id) {
            throw InvalidRequestException("담당자를 등록하려고 하는 유저가 유효하지 않거나, 일정을 만든 유저가 아닙니다.")
        }

        val managerUser = userRepository.getUserById(managerSaveRequest.managerUserId)
            ?: throw InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다.")

        if (user.id == managerUser.id) {
            throw InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다.")
        }

        val newManagerUser = Manager(
            user = managerUser,
            todo = todo
        )
        val savedManagerUser = managerRepository.save(newManagerUser)

        runCatching {
            logService.saveLog(
                requestUserId = user.id,
                managerUserId = managerUser.id,
                todoId = todo.id
            )
        }.onFailure { e ->
            log.error("Log 저장 실패 :  ${e.message}", e)
        }

        return ManagerSaveResponse(
            id = savedManagerUser.id,
            user = UserResponse(
                id = managerUser.id,
                email = managerUser.email
            )
        )
    }

    fun getManagers(todoId: Long): List<ManagerResponse> {
        val todo = todoRepository.getTodoById(todoId)
            ?: throw InvalidRequestException("Todo not found")

        return managerRepository.findWithUserByTodoId(todo.id)
            .map { manager ->
                ManagerResponse(
                    id = manager.id,
                    user = UserResponse(
                        id = manager.user.id,
                        email = manager.user.email
                    )
                )
            }
    }

    @Transactional
    fun deleteManager(authUser: AuthUser, todoId: Long, managerId: Long) {
        val user = User.fromAuthUser(authUser)

        val todo = todoRepository.findByIdWithUser(todoId)
            ?: throw InvalidRequestException("Todo not found")

        if (user.id != todo.user.id) {
            throw InvalidRequestException("해당 일정을 만든 유저가 유효하지 않습니다.")
        }

        val manager = managerRepository.findWithTodoById(managerId)
            ?: throw InvalidRequestException("Manager not found")

        if (todo.id != manager.todo.id) {
            throw InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다.")
        }

        managerRepository.delete(manager)
    }
}
