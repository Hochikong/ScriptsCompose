package me.ckho.scriptscompose.deprecated.service.impl


import me.ckho.scriptscompose.deprecated.service.AuthService
import me.ckho.scriptscompose.repository.UsersRepository
import me.ckho.scriptscompose.utils.Encryption
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(@Autowired private val userDAO: UsersRepository) : AuthService {
    override fun isAuthPass(username: String, password: String): Boolean {
        val user = userDAO.findUserByUsername(username)
        return Encryption.verify(password, user.password_hash)
    }
}