package me.ckho.scriptscompose.controller

import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HashController {
    val passwordEncoder: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @GetMapping("/utils/hash", produces = ["application/json"])
    fun cal(raw: String): String {
        return passwordEncoder.encode(raw)
    }
}