package org.example.expert.domain.user.repository;

import java.util.Optional;

import org.example.expert.domain.user.dto.response.UserSearchResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	@Cacheable(value = "userByNickname", key = "#nickname", unless = "#result == null")
	@Query("SELECT u FROM User u WHERE u.nickname = :nickname")
	Optional<User> findByNickname(String nickname);

	@Cacheable(value = "userByNickname1", key = "#nickname", unless = "#result == null")
	@Query("SELECT u FROM User u WHERE u.nickname = :nickname")
	Optional<User> findByNicknameUsingCache(String nickname);

	@Cacheable(value = "userByNickname2", key = "#nickname", unless = "#result == null")
	@Query("SELECT new org.example.expert.domain.user.dto.response.UserSearchResponse(u.id, u.nickname) FROM User u WHERE u.nickname = :nickname")
	Optional<UserSearchResponse> findUserIdAndUserNicknameByNicknameUsingCache(String nickname);

	Optional<User> findUserByNickname(String nickname);
}
