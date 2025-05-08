package org.example.expert.config


import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException

class JwtFilterException(
    val errorCode: HttpStatus,
    override val message: String?,
) : AuthenticationException(message)
