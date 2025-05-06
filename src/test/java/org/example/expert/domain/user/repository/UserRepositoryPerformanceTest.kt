package org.example.expert.domain.user.repository

import org.assertj.core.api.Assertions.assertThat
import org.example.expert.domain.user.dto.response.UserSearchResponse
import org.example.expert.domain.user.entity.User
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserRepositoryPerformanceTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    @Transactional(readOnly = true)
    @Rollback(false)
    fun performanceTest() {
        val totalCount: Long = userRepository.count()
        println("총 데이터 수: $totalCount 건")

        val targetNickname = "test_user_99999"

        // 웜업 - JVM 최적화를 위한 사전 실행
        userRepository.findUserByNickname(targetNickname)
        userRepository.findByNicknameUsingCache(targetNickname)
        userRepository.findUserIdAndUserNicknameByNicknameUsingCache(targetNickname)

        // 가비지 컬렉션 실행으로 테스트 환경 정리
        System.gc()
        runCatching {
            Thread.sleep(1000)
        }.onFailure {
            Thread.currentThread().interrupt()
        }

        // 각 메서드 10회 반복 테스트
        val iterations = 10
        val executionTimes = mutableMapOf(
            "일반조회" to mutableListOf<Long>(),
            "캐시전체" to mutableListOf(),
            "캐시부분" to mutableListOf()
        )

        repeat(iterations) {
            // 일반 조회
            var start: Long = System.nanoTime()
            val user1: User? = userRepository.findUserByNickname(targetNickname)
            executionTimes["일반조회"]?.add(System.nanoTime() - start)

            // 캐시 전체 데이터 조회
            start = System.nanoTime()
            val user2: User? = userRepository.findByNicknameUsingCache(targetNickname)
            executionTimes["캐시전체"]?.add(System.nanoTime() - start)

            // 캐시 부분 데이터 조회
            start = System.nanoTime()
            val user3: UserSearchResponse? =
                userRepository.findUserIdAndUserNicknameByNicknameUsingCache(targetNickname)
            executionTimes["캐시부분"]?.add(System.nanoTime() - start)

            // 결과 검증
            assertThat(user1?.nickname)
                .isEqualTo(user2?.nickname)
                .isEqualTo(user3?.nickname)
        }

        // 결과 출력
        executionTimes.forEach { (method, times) ->
            val avgMillis = times.average() / 1000000.0
            val minMillis = times.minOrNull()?.div(1000000.0) ?: 0.0
            val maxMillis = times.maxOrNull()?.div(1000000.0) ?: 0.0

            println("\n=== $method 성능 통계 ===")
            println("평균 실행 시간: " + String.format("%.2f", avgMillis) + " ms")
            println("최소 실행 시간: " + String.format("%.2f", minMillis) + " ms")
            println("최대 실행 시간: " + String.format("%.2f", maxMillis) + " ms")
            println("==================\n")
        }
    }

    @Test
    @Transactional
    @Rollback(false)
    fun generateTestData() {
        val batchSize = 1000
        val totalSize = 1000000

        (0 until totalSize step batchSize).forEach { i ->
            val users = List(batchSize) { j ->
                val userNumber: Int = i + j
                User(
                    nickname = "test_user_$userNumber",
                    email = "test_user_$userNumber@example.com"
                )
            }
            userRepository.saveAll(users)

            if (i % 10000 == 0) {
                println("${i}건 저장 완료")
            }
        }
    }
}