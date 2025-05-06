package org.example.expert.domain.todo.entity

import jakarta.persistence.*
import org.example.expert.domain.comment.entity.Comment
import org.example.expert.domain.common.entity.Timestamped
import org.example.expert.domain.manager.entity.Manager
import org.example.expert.domain.user.entity.User

@Entity
@Table(name = "todos")
class Todo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var title: String? = null,

    var contents: String? = null,

    var weather: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) : Timestamped() {
    @OneToMany(mappedBy = "todo", cascade = [CascadeType.REMOVE])
    val comments: MutableList<Comment> = mutableListOf()

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.ALL])
    val managers: MutableList<Manager?> = mutableListOf()

    init {
        this.managers.add(
            Manager(
                user = user,
                todo = this
            )
        )
    }
}
