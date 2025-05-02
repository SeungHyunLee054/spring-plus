package org.example.expert.domain.user.controller;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/users/{userId}")
	public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
		return ResponseEntity.ok(userService.getUser(userId));
	}

	@PutMapping("/users")
	public void changePassword(@AuthenticationPrincipal AuthUser authUser,
		@RequestBody UserChangePasswordRequest userChangePasswordRequest) {
		userService.changePassword(authUser.getId(), userChangePasswordRequest);
	}

	@PutMapping(value = "/users/profileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void changeProfileImage(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestPart("profileImage") MultipartFile profileImage
	) {
		userService.changeProfileImage(authUser.getId(), profileImage);
	}
}
