package me.ckho.scriptscompose.controller

import me.ckho.scriptscompose.service.impl.LowLevelDBOps
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestingController(
    @Autowired
    val dbOps: LowLevelDBOps
) {
    @GetMapping("/testing/trigger", produces = ["application/json"])
    fun cal(key: String): String {
        dbOps.recreateScriptGroupsCacheTable()
        return "Done"
    }
}