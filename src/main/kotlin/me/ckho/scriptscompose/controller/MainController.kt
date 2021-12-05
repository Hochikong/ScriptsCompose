package me.ckho.scriptscompose.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainController {


    @GetMapping("/")
    fun home(): String {
        return "index"
    }

    @GetMapping("/cronDash")
    fun cron(): String{
        return "cronDash"
    }

    @GetMapping("/oneDash")
    fun one(): String{
        return "oneDash"
    }
}