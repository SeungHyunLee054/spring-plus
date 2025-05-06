package org.example.expert.domain.log.entity

import jakarta.persistence.*
import org.example.expert.domain.common.entity.Timestamped

@Entity
@Table(name = "logs")
class Log(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var requestUserId: Long? = null,

    var managerUserId: Long? = null,

    var todoId: Long? = null,
) : Timestamped()
