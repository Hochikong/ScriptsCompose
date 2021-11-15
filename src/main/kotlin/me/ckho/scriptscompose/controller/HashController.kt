package me.ckho.scriptscompose.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HashController {
    val passwordEncoder: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    var logger: Logger = LoggerFactory.getLogger(HashController::class.java)

    @GetMapping("/utils/hash", produces = ["application/json"])
    fun cal(raw: String): String {
        val p = passwordEncoder.encode(raw)
        logger.info("A TRACE Message -> $p");
        return p
    }
}