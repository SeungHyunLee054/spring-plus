package org.example.expert.domain.log.service;

import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {
	private final LogRepository logRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveLog(Long requestUserId, Long managerUserId, Long todoId) {
		Log log = Log.builder()
			.requestUserId(requestUserId)
			.managerUserId(managerUserId)
			.todoId(todoId)
			.build();

		logRepository.save(log);
	}
}
