package org.example.expert.domain.user.repository

import org.example.expert.domain.user.dto.response.UserSearchResponse
import org.example.expert.domain.user.entity.User
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long> {
    fun getUserById(id: Long): User?

    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean

    fun findByNickname(nickname: String): User?

    /*
    테스트용 메서드들
     */
    @Cacheable(value = ["userByNickname1"], key = "#nickname", unless = "#result == null")
    @Query("SELECT u FROM User u WHERE u.nickname = :nickname")
    fun findByNicknameUsingCache(nickname: String): User?

    @Cacheable(value = ["userByNickname2"], key = "#nickname", unless = "#result == null")
    @Query(
        """
        SELECT new org.example.expert.domain.user.dto.response.UserSearchResponse(u.id, u.nickname) 
        FROM User u 
        WHERE u.nickname = :nickname
        """
    )
    fun findUserIdAndUserNicknameByNicknameUsingCache(nickname: String): UserSearchResponse?

    fun findUserByNickname(nickname: String): User?
}
