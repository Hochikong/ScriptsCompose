package me.ckho.scriptscompose.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class MainController {


    @GetMapping("/")
    fun home(): String {
        return "index"
    }

    @GetMapping("/cronDash")
    fun cron(): String {
        return "cronDash"
    }

    @GetMapping("/oneDash")
    fun one(): String {
        return "oneDash"
    }

    @GetMapping("/taskDetails")
    fun taskDetail(@RequestParam(value = "taskHash", required = true) taskHash: String, model: Model): String {
        model.addAttribute("task_hash", taskHash)
        return "taskDetails"
    }
}