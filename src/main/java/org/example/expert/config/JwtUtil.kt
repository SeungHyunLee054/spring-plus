package org.example.expert.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.example.expert.domain.common.exception.ServerException
import org.example.expert.domain.user.enums.UserRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.security.Key
import java.util.*

@Component
class JwtUtil(@Value("\${jwt.secret.key}") private val secretKey: String) {
    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val TOKEN_TIME = 60 * 60 * 1000L // 60분
        private val signatureAlgorithm = SignatureAlgorithm.HS256
    }

    private val key: Key by lazy {
        secretKey.let {
            Base64.getDecoder().decode(it)
        }.let { bytes ->
            Keys.hmacShaKeyFor(bytes)
        }
    }

    fun createToken(userId: Long?, email: String?, userRole: UserRole?, nickname: String?): String =
        Date().let { date ->
            BEARER_PREFIX +
                    Jwts.builder()
                        .setSubject(userId.toString())
                        .claim("email", email)
                        .claim("userRole", userRole)
                        .claim("nickname", nickname)
                        .setExpiration(Date(date.time + TOKEN_TIME))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact()
        }

    fun substringToken(tokenValue: String): String =
        when {
            StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX) ->
                tokenValue.substring(7)

            else -> throw ServerException("Not Found Token")
        }

    fun extractClaims(token: String): Claims? {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
    }
}
