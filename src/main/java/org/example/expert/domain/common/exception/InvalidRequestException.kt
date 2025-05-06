package org.example.expert.domain.common.exception

class InvalidRequestException(
    override val message: String
) : RuntimeException(message)
