package org.example.expert.domain.common.exception

class ServerException(
    override val message: String
) : RuntimeException(message)
