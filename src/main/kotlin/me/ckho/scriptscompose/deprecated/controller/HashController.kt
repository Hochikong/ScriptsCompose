package me.ckho.scriptscompose.deprecated.controller

import me.ckho.scriptscompose.utils.Encryption
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HashController {
    @GetMapping("/utils/hash", produces = ["application/json"])
    fun cal(raw: String): String {
        return Encryption.encrypt(raw)
    }

    @GetMapping("/utils/validate", produces = ["application/json"])
    fun validate(raw: String, hash: String): Int {
        val converted = hash.replace(" ", "+")
        return if (Encryption.verify(raw, converted)) {
            0
        } else {
            -1
        }
    }
}