package org.example.expert.domain.user.controller

import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.dto.response.UserSearchResponse
import org.example.expert.domain.user.service.UserService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class UserController(private val userService: UserService) {

    @GetMapping("/users/{userId}")
    fun getUser(@PathVariable userId: Long): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.getUser(userId))

    @GetMapping("/users")
    fun getUserByNickname(@RequestParam nickname: String): ResponseEntity<UserSearchResponse> =
        ResponseEntity.ok(userService.getUserByNickname(nickname))

    @PutMapping("/users")
    fun changePassword(
        @AuthenticationPrincipal authUser: AuthUser,
        @RequestBody userChangePasswordRequest: UserChangePasswordRequest,
    ) {
        userService.changePassword(authUser.id, userChangePasswordRequest)
    }

    @PutMapping(value = ["/users/profileImage"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun changeProfileImage(
        @AuthenticationPrincipal authUser: AuthUser,
        @RequestPart("profileImage") profileImage: MultipartFile,
    ) {
        userService.changeProfileImage(authUser.id, profileImage)
    }
}
