package org.example.expert.aop

import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import java.time.LocalDateTime


@Aspect
@Component
class AdminAccessLoggingAspect(private val request: HttpServletRequest) {
    private val log = org.slf4j.LoggerFactory.getLogger(AdminAccessLoggingAspect::class.java)

    @Before("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    fun logBeforeChangeUserRole(joinPoint: JoinPoint) {
        val userId: String? = request.getAttribute("userId").toString()
        val requestUrl = request.requestURI
        val requestTime = LocalDateTime.now()

        log.info(
            "Admin Access Log - User ID: {}, Request Time: {}, Request URL: {}, Method: {}",
            userId, requestTime, requestUrl, joinPoint.signature.name
        )
    }
}
