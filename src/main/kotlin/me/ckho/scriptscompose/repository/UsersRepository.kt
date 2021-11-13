package me.ckho.scriptscompose.repository

import me.ckho.scriptscompose.domain.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(name: String): UserEntity?
}