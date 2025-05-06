package org.example.expert.domain.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.example.expert.domain.common.validator.ImageFile
import org.springframework.web.multipart.MultipartFile

data class SignupRequest(
    @NotBlank
    @Email
    val email: String,

    @NotBlank
    val password: String,

    @NotBlank
    val nickname: String,

    @NotBlank
    val userRole: String,

    @ImageFile
    val profileImage: MultipartFile
)
