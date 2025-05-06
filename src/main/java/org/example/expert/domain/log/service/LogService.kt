package org.example.expert.domain.log.service

import org.example.expert.domain.log.entity.Log
import org.example.expert.domain.log.repository.LogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class LogService(private val logRepository: LogRepository) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveLog(requestUserId: Long?, managerUserId: Long?, todoId: Long?) {
        val log = Log(
            requestUserId = requestUserId,
            managerUserId = managerUserId,
            todoId = todoId
        )

        logRepository.save(log)
    }
}
