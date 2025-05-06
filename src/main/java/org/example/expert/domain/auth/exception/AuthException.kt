package org.example.expert.domain.auth.exception

class AuthException(
    override val message: String,
) : RuntimeException(message)
