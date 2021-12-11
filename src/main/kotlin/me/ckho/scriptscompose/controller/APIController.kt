package me.ckho.scriptscompose.controller

import me.ckho.scriptscompose.repository.ScriptLogsRepository
import me.ckho.scriptscompose.service.impl.ScriptsConfigLoaderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class APIController(
    @Autowired
    var sle: ScriptLogsRepository,
    @Autowired
    var scls: ScriptsConfigLoaderService
) {

    val passwordEncoder: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    //    private val oneTimeDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var logger: Logger = LoggerFactory.getLogger(APIController::class.java)

    @GetMapping("/utils/hash", produces = ["application/json"])
    fun cal(raw: String): String {
        val p = passwordEncoder.encode(raw)
        logger.info("A TRACE Message -> $p");
        return p
    }

    @GetMapping("/groups/allGroups", produces = ["application/json"])
    fun getAllGroups(): Map<String, Any> {
        val r = scls.getAllGroupsFromSCG()
        return mapOf(
            "groups" to r,
            "message" to "Query done.",
            "code" to 200
        )
    }

    @GetMapping("/groups/allGroups/byType", produces = ["application/json"])
    fun getAllGroupsByType(type: String): Map<String, Any> {
        return if (type != "cron" && type != "one") {
            mapOf(
                "groups" to "none",
                "message" to "Unsupported type.",
                "code" to 400
            )
        } else {
            val r = scls.getGroupsByType(type)
            mapOf(
                "groups" to r,
                "message" to "Query done.",
                "code" to 200
            )
        }
    }

    /**
     * Return all registered tasks
     * */
    @GetMapping("/tasks/allTasks", produces = ["application/json"])
    fun getTasks(): Map<String, Any> {
        val r = scls.getAllTasksFromSCG()
        return mapOf(
            "tasks" to r,
            "message" to "Query done.",
            "code" to 200
        )
    }

    /**
     * Return all registered tasks by type
     * */
    @GetMapping("/tasks/allTasks/byType", produces = ["application/json"])
    fun getTasksByType(type: String): Map<String, Any> {
        return if (type != "cron" && type != "one") {
            mapOf(
                "tasks" to "none",
                "message" to "Unsupported type.",
                "code" to 400
            )
        } else {
            val r = scls.getAllTasksByType(type)
            return mapOf(
                "tasks" to r,
                "message" to "Query done.",
                "code" to 200
            )
        }
    }

    @GetMapping("/tasks/allRunning", produces = ["application/json"])
    fun getAllRunningTasks(): Map<String, Any> {
        return mapOf(
            "tasks" to scls.getAllRunningTasks(),
            "message" to "Query done.",
            "code" to 200
        )
    }
}