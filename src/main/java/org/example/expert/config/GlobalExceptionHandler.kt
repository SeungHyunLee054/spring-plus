package org.example.expert.config

import org.example.expert.domain.auth.exception.AuthException
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.common.exception.ServerException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(InvalidRequestException::class)
    fun invalidRequestExceptionException(ex: InvalidRequestException): ResponseEntity<Map<String, Any>> {
        val status = HttpStatus.BAD_REQUEST
        return getErrorResponse(status, ex.message)
    }

    @ExceptionHandler(AuthException::class)
    fun handleAuthException(ex: AuthException): ResponseEntity<Map<String, Any>> {
        val status = HttpStatus.UNAUTHORIZED
        return getErrorResponse(status, ex.message)
    }

    @ExceptionHandler(ServerException::class)
    fun handleServerException(ex: ServerException): ResponseEntity<Map<String, Any>> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        return getErrorResponse(status, ex.message)
    }

    fun getErrorResponse(status: HttpStatus, message: String) = ResponseEntity(
        buildMap<String, Any> {
            put("status", status.name)
            put("code", status.value())
            put("message", message)
        },
        status
    )

}

