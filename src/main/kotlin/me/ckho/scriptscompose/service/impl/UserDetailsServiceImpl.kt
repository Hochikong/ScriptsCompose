package me.ckho.scriptscompose.service.impl

import me.ckho.scriptscompose.repository.UsersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class UserDetailsServiceImpl(
    @Autowired
    private val userRepository: UsersRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
        if (user != null){
            return user
        }else{
            throw UsernameNotFoundException("找不到该用户名")
        }
    }
}