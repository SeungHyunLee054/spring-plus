package org.example.expert.config

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.user.enums.UserRole
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtFilter(private val jwtUtil: JwtUtil) : OncePerRequestFilter() {
    companion object {
        private val log = LoggerFactory.getLogger(JwtFilter::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val url = request.requestURI

        if (url.startsWith("/swagger") || url.startsWith("/v3/api-docs") || url.startsWith("/swagger-resources") ||
            url.startsWith("/actuator")
        ) {
            filterChain.doFilter(request, response)
            return
        }

        if (url.startsWith("/auth")) {
            filterChain.doFilter(request, response)
            return
        }

        val bearerJwt = request.getHeader("Authorization") ?: run {
            // 토큰이 없는 경우 400을 반환합니다.
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.")
            return
        }

        val jwt = jwtUtil.substringToken(bearerJwt)

        runCatching {
            val authentication = processJwtToken(jwt) ?: run {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.")
                return
            }

            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)

        }.onFailure { e ->
            handleJwtError(e, response)
        }
    }

    private fun processJwtToken(jwt: String): Authentication? {
        // JWT 유효성 검사와 claims 추출
        val claims = jwtUtil.extractClaims(jwt) ?: return null


        val userRole = UserRole.valueOf(claims.get("userRole", String::class.java))
        val authUser = AuthUser(
            claims.subject.toLong(),
            claims.get("email", String::class.java),
            userRole
        )

        return UsernamePasswordAuthenticationToken(
            authUser,
            null,
            listOf(SimpleGrantedAuthority("ROLE_${userRole.name}"))
        )
    }

    private fun handleJwtError(e: Throwable, response: HttpServletResponse) {
        when (e) {
            is SecurityException,
            is MalformedJwtException -> sendError(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "유효하지 않는 JWT 서명입니다.",
                e
            )

            is ExpiredJwtException -> sendError(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "만료된 JWT 토큰입니다.",
                e
            )

            is UnsupportedJwtException -> sendError(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                "지원되지 않는 JWT 토큰입니다.",
                e
            )

            else -> {
                log.error("Internal server error", e)
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            }
        }
    }

    private fun sendError(
        response: HttpServletResponse,
        status: Int,
        message: String,
        e: Throwable
    ) {
        log.error(message, e)
        response.sendError(status, message)
    }
}