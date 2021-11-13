package me.ckho.scriptscompose.domain

import javax.persistence.*

@Entity
@Table(name = "register_users")
class UserEntity(
    @Column(unique=true)
    var username: String,
    var password_hash: String,
    @Id @GeneratedValue var id: Long? = null
) {
    override fun toString(): String {
        return "UserEntity(username='$username', password_hash='$password_hash', id=$id)"
    }
}