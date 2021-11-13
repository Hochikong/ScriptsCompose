package me.ckho.scriptscompose.controller

import me.ckho.scriptscompose.deprecated.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainController(@Autowired private val authService: AuthService) {
//    @PostMapping("/login", produces = ["application/json"], consumes = ["application/json"])
//    fun login(@RequestBody loginBody: LoginBody){
//        if (authService.isAuthPass(loginBody.username, loginBody.password)){
//
//        }
//    }

    @GetMapping("/")
    fun home(): String {
        return "index"
    }
}