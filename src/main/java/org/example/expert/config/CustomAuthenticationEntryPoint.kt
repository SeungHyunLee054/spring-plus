package org.example.expert.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

class CustomAuthenticationEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {
    private val log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint::class.java)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        val exception = request.getAttribute("exception") as? JwtFilterException

        val errorResponse = exception?.let {
            log.error(exception.message, exception)
            ErrorResponse(
                status = it.errorCode.value(),
                message = it.message
            )
        } ?: run {
            log.error(authException.message, authException)
            ErrorResponse(
                status = HttpStatus.UNAUTHORIZED.value(),
                message = "인증에 실패했습니다."
            )
        }

        response.status = errorResponse.status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}