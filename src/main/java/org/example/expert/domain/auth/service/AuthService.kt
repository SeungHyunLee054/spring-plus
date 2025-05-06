package org.example.expert.domain.auth.service

import org.example.expert.client.s3.S3Service
import org.example.expert.config.JwtUtil
import org.example.expert.config.PasswordEncoder
import org.example.expert.domain.auth.dto.request.SigninRequest
import org.example.expert.domain.auth.dto.request.SignupRequest
import org.example.expert.domain.auth.dto.response.SigninResponse
import org.example.expert.domain.auth.dto.response.SignupResponse
import org.example.expert.domain.auth.exception.AuthException
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.user.entity.User
import org.example.expert.domain.user.enums.UserRole
import org.example.expert.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val s3Service: S3Service
) {

    @Transactional
    fun signup(signupRequest: SignupRequest): SignupResponse {
        if (userRepository.existsByEmail(signupRequest.email)) {
            throw InvalidRequestException("이미 존재하는 이메일입니다.")
        }

        val encodedPassword = passwordEncoder.encode(signupRequest.password)

        val userRole = UserRole.of(signupRequest.userRole)

        val profileImageUrl = s3Service.uploadAndGetUrl(signupRequest.profileImage)

        val newUser = User(
            email = signupRequest.email,
            password = encodedPassword,
            nickname = signupRequest.nickname,
            profileImage = profileImageUrl,
            userRole = userRole
        )

        val savedUser = userRepository.save(newUser)

        val bearerToken = jwtUtil.createToken(
            userId = savedUser.id,
            email = savedUser.email,
            userRole = userRole,
            nickname = savedUser.nickname
        )

        return SignupResponse(bearerToken)
    }

    fun signin(signinRequest: SigninRequest): SigninResponse {
        val user = userRepository.findByEmail(signinRequest.email)
            ?: throw InvalidRequestException("가입되지 않은 유저입니다.")

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(signinRequest.password, user.password)) {
            throw AuthException("잘못된 비밀번호입니다.")
        }

        val bearerToken = jwtUtil.createToken(
            userId = user.id,
            email = user.email,
            userRole = user.userRole,
            nickname = user.nickname
        )

        return SigninResponse(bearerToken)
    }
}
