package me.ckho.scriptscompose.repository

import me.ckho.scriptscompose.domain.UserEntity
import org.springframework.data.repository.CrudRepository

interface UsersRepository : CrudRepository<UserEntity, Long> {
    fun findUserByUsername(name: String): UserEntity
    fun findUserById(id: Long): UserEntity
}