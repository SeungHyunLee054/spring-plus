package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends JpaRepository<Todo, Long> {

	@Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u"
		+ " WHERE (:weather IS NULL OR t.weather = :weather)"
		+ " AND (:modifiedFrom IS NULL OR :modifiedTo IS NULL OR t.modifiedAt between :modifiedFrom AND :modifiedTo)"
		+ " ORDER BY t.modifiedAt DESC")
	Page<Todo> findTodosByWeatherAndDateRange(
		@Param("weather") String weather,
		@Param("modifiedFrom") LocalDateTime modifiedFrom,
		@Param("modifiedTo") LocalDateTime modifiedTo,
		Pageable pageable
	);

	@Query("SELECT t FROM Todo t "
		+ "LEFT JOIN t.user "
		+ "WHERE t.id = :todoId")
	Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}
