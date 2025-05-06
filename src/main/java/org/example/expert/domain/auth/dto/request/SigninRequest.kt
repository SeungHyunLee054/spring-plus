package org.example.expert.domain.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SigninRequest(
    @NotBlank
    @Email
    val email: String,

    @NotBlank
    val password: String
)
