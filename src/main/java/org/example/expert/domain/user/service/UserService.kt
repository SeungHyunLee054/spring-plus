package org.example.expert.domain.user.service

import org.example.expert.client.s3.S3Service
import org.example.expert.config.PasswordEncoder
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.dto.response.UserSearchResponse
import org.example.expert.domain.user.repository.UserRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val s3Service: S3Service,
) {

    fun getUser(userId: Long): UserResponse {
        val user = userRepository.getUserById(userId) ?: throw InvalidRequestException("User not found")

        return UserResponse(user.id, user.email)
    }

    @Cacheable(value = ["userByNickname"], key = "#nickname")
    @Transactional(readOnly = true)
    fun getUserByNickname(nickname: String): UserSearchResponse {
        val user = userRepository.findByNickname(nickname) ?: throw InvalidRequestException("User not found")

        return UserSearchResponse(user.id, user.nickname)
    }

    @Transactional
    fun changePassword(userId: Long, userChangePasswordRequest: UserChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest)

        val user = userRepository.getUserById(userId) ?: throw InvalidRequestException("User not found")

        if (passwordEncoder.matches(userChangePasswordRequest.newPassword, user.password)) {
            throw InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.")
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.oldPassword, user.password)) {
            throw InvalidRequestException("잘못된 비밀번호입니다.")
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.newPassword))
    }

    @Transactional
    fun changeProfileImage(userId: Long, profileImage: MultipartFile) {
        val user = userRepository.getUserById(userId) ?: throw InvalidRequestException("User not found")

        s3Service.deleteFileIfPresent(user.profileImage)

        val profileImageUrl = s3Service.uploadAndGetUrl(profileImage)

        user.changeProfileImage(profileImageUrl)
    }

    companion object {
        private fun validateNewPassword(userChangePasswordRequest: UserChangePasswordRequest) {
            val newPassword = userChangePasswordRequest.newPassword
            if (newPassword.length < 8 ||
                !newPassword.contains(Regex("\\d")) ||
                !newPassword.contains(Regex("[A-Z]"))
            ) {
                throw InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.")
            }
        }
    }
}