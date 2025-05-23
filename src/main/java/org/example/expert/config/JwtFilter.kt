package org.example.expert.config

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.user.enums.UserRole
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtFilter(private val jwtUtil: JwtUtil, private val entryPoint: CustomAuthenticationEntryPoint) :
    OncePerRequestFilter() {
    companion object {
        private val WHITE_LIST = listOf("/swagger", "/v3/api-docs", "/swagger-resources", "/actuator", "/auth")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val url = request.requestURI

        if (WHITE_LIST.stream().anyMatch { url.startsWith(it) }) {
            filterChain.doFilter(request, response)
            return
        }

        // 토큰이 없는 경우 400을 반환합니다.
        val bearerJwt = request.getHeader("Authorization") ?: run {
            SecurityContextHolder.clearContext()
            entryPoint.commence(request, response, JwtFilterException(HttpStatus.BAD_REQUEST, "JWT 토큰이 필요합니다."))
            return
        }

        val jwt = jwtUtil.substringToken(bearerJwt)
        runCatching {
            val authentication = processJwtToken(jwt) ?: run {
                SecurityContextHolder.clearContext()
                entryPoint.commence(request, response, JwtFilterException(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰입니다."))
                return
            }

            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)

        }.onFailure { e ->
            handleJwtError(e, request, response)
            return
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

    private fun handleJwtError(e: Throwable, request: HttpServletRequest, response: HttpServletResponse) {
        when (e) {
            is SecurityException,
            is MalformedJwtException,
                -> {
                SecurityContextHolder.clearContext()
                entryPoint.commence(
                    request,
                    response,
                    JwtFilterException(HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.")
                )
            }

            is ExpiredJwtException -> {
                SecurityContextHolder.clearContext()
                entryPoint.commence(request, response, JwtFilterException(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."))
            }

            is UnsupportedJwtException -> {
                SecurityContextHolder.clearContext()
                entryPoint.commence(request, response, JwtFilterException(HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰입니다."))
            }

            else -> {
                SecurityContextHolder.clearContext()
                entryPoint.commence(
                    request,
                    response,
                    JwtFilterException(HttpStatus.INTERNAL_SERVER_ERROR, ": ${e.message}")
                )
            }
        }
    }
}