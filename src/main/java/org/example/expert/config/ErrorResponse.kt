package org.example.expert.config

import java.time.LocalDateTime

data class ErrorResponse(
    val status: Int,
    val message: String?,
    val timestamp: String = LocalDateTime.now().toString(),
)
