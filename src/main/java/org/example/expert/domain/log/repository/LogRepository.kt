package org.example.expert.domain.log.repository

import org.example.expert.domain.log.entity.Log
import org.springframework.data.jpa.repository.JpaRepository

interface LogRepository : JpaRepository<Log, Long>
