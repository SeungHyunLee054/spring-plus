package org.example.expert.domain.user.entity

import jakarta.persistence.*
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.entity.Timestamped
import org.example.expert.domain.user.enums.UserRole
import org.hibernate.annotations.DynamicInsert


@Entity
@Table(name = "users", indexes = [Index(name = "idx_user_nickname", columnList = "nickname")])
@DynamicInsert
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true)
    var email: String,

    var password: String? = null,

    var nickname: String? = null,

    var profileImage: String? = null,

    @Enumerated(EnumType.STRING)
    var userRole: UserRole? = null
) : Timestamped() {

    fun changePassword(password: String) {
        this.password = password
    }

    fun changeProfileImage(profileImage: String?) {
        this.profileImage = profileImage
    }

    fun updateRole(userRole: UserRole) {
        this.userRole = userRole
    }

    companion object {
        fun fromAuthUser(authUser: AuthUser) = User(
            id = authUser.id,
            email = authUser.email,
            userRole = authUser.userRole
        )
    }
}
