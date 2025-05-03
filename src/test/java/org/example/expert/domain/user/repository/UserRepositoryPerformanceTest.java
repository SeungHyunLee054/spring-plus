package org.example.expert.domain.user.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.expert.domain.user.dto.response.UserSearchResponse;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryPerformanceTest {
	@Autowired
	private UserRepository userRepository;

	@Test
	@Transactional(readOnly = true)
	@Rollback(false)
	void performanceTest() {
		long totalCount = userRepository.count();
		System.out.println("총 데이터 수: " + totalCount + " 건");

		String targetNickname = "test_user_99999";

		// 웜업 - JVM 최적화를 위한 사전 실행
		userRepository.findUserByNickname(targetNickname).get();
		userRepository.findByNicknameUsingCache(targetNickname).get();
		userRepository.findUserIdAndUserNicknameByNicknameUsingCache(targetNickname).get();

		// 가비지 컬렉션 실행으로 테스트 환경 정리
		System.gc();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		// 각 메서드 10회 반복 테스트
		int iterations = 10;
		Map<String, List<Long>> executionTimes = new HashMap<>();
		executionTimes.put("일반조회", new ArrayList<>());
		executionTimes.put("캐시전체", new ArrayList<>());
		executionTimes.put("캐시부분", new ArrayList<>());

		for (int i = 0; i < iterations; i++) {
			// 일반 조회
			long start = System.nanoTime();
			User user1 = userRepository.findUserByNickname(targetNickname).get();
			executionTimes.get("일반조회").add(System.nanoTime() - start);

			// 캐시 전체 데이터 조회
			start = System.nanoTime();
			User user2 = userRepository.findByNicknameUsingCache(targetNickname).get();
			executionTimes.get("캐시전체").add(System.nanoTime() - start);

			// 캐시 부분 데이터 조회
			start = System.nanoTime();
			UserSearchResponse user3 = userRepository.findUserIdAndUserNicknameByNicknameUsingCache(
					targetNickname)
				.get();
			executionTimes.get("캐시부분").add(System.nanoTime() - start);

			// 결과 검증
			assertThat(user1.getNickname())
				.isEqualTo(user2.getNickname())
				.isEqualTo(user3.getNickname());
		}

		// 결과 출력
		executionTimes.forEach((method, times) -> {
			double avgMillis = times.stream()
				.mapToDouble(t -> t / 1_000_000.0)
				.average()
				.orElse(0.0);
			double minMillis = times.stream()
				.mapToDouble(t -> t / 1_000_000.0)
				.min()
				.orElse(0.0);
			double maxMillis = times.stream()
				.mapToDouble(t -> t / 1_000_000.0)
				.max()
				.orElse(0.0);

			System.out.println("\n=== " + method + " 성능 통계 ===");
			System.out.println("평균 실행 시간: " + String.format("%.2f", avgMillis) + " ms");
			System.out.println("최소 실행 시간: " + String.format("%.2f", minMillis) + " ms");
			System.out.println("최대 실행 시간: " + String.format("%.2f", maxMillis) + " ms");
			System.out.println("==================\n");
		});
	}

	@Test
	@Transactional
	@Rollback(false)
	void generateTestData() {
		int batchSize = 1000;
		int totalSize = 1_000_000;

		for (int i = 0; i < totalSize; i += batchSize) {
			List<User> users = new ArrayList<>();
			for (int j = 0; j < batchSize; j++) {
				int userNumber = i + j;
				users.add(User.builder()
					.nickname("test_user_" + userNumber)
					.build());
			}
			userRepository.saveAll(users);

			if (i % 10000 == 0) {
				System.out.println(i + "건 저장 완료");
			}
		}
	}
}