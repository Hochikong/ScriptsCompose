package me.ckho.scriptscompose.controller

import me.ckho.scriptscompose.service.impl.LowLevelDBOps
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestingController(
    @Autowired
    val dbOps: LowLevelDBOps
) {
    //    private val oneTimeDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var logger: Logger = LoggerFactory.getLogger(APIController::class.java)

    /**
     * Generate password hash
     * */
    @GetMapping("/testing/trigger", produces = ["application/json"])
    fun cal(key: String): String {
        dbOps.recreateScriptGroupsCacheTable()
        return "Done"
    }
}