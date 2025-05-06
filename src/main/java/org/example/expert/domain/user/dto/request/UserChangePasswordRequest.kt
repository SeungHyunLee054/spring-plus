package org.example.expert.domain.user.dto.request

import jakarta.validation.constraints.NotBlank

data class UserChangePasswordRequest(
    @NotBlank
    val oldPassword: String,

    @NotBlank
    val newPassword: String,
)
