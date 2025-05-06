package org.example.expert.domain.auth.dto.request;

import org.example.expert.domain.common.validator.ImageFile;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;

	@NotBlank
	private String nickname;

	@NotBlank
	private String userRole;

	@ImageFile
	private MultipartFile profileImage;
}
