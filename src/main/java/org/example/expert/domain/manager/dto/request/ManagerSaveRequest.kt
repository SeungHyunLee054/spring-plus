package org.example.expert.domain.manager.dto.request

import jakarta.validation.constraints.NotNull

data class ManagerSaveRequest(
    @NotNull
    val managerUserId: Long, // 일정 작상자가 배치하는 유저 id
)
