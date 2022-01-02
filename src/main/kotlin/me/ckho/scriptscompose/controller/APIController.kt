package me.ckho.scriptscompose.controller

import me.ckho.scriptscompose.repository.ScriptLogsRepository
import me.ckho.scriptscompose.service.impl.DataCache
import me.ckho.scriptscompose.service.impl.ScriptLogsQueryService
import me.ckho.scriptscompose.service.impl.ScriptsConfigLoaderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class APIController(
    @Autowired
    var sle: ScriptLogsRepository,
    @Autowired
    var scls: ScriptsConfigLoaderService,
    @Autowired
    val cache: DataCache,
    @Autowired
    val slqs: ScriptLogsQueryService
) {

    val passwordEncoder: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    //    private val oneTimeDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var logger: Logger = LoggerFactory.getLogger(APIController::class.java)

    /**
     * Generate password hash
     * */
    @GetMapping("/utils/hash", produces = ["application/json"])
    fun cal(raw: String): String {
        val p = passwordEncoder.encode(raw)
        logger.info("A TRACE Message -> $p");
        return p
    }

    /**
     * List all script groups
     * */
    @GetMapping("/groups/allGroups", produces = ["application/json"])
    fun getAllGroups(): Map<String, Any> {
        val r = scls.getAllGroupsFromSCG()
        return mapOf(
            "groups" to r,
            "message" to "Query done.",
            "code" to 200
        )
    }

    /**
     * List all script groups by job type
     * */
    @GetMapping("/groups/allGroups/byType", produces = ["application/json"])
    fun getAllGroupsByType(type: String): Map<String, Any> {
        return if (type != "cron" && type != "one") {
            mapOf(
                "groups" to "none",
                "message" to "Unsupported type.",
                "code" to 403
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

    @GetMapping("/tasks/detail", produces = ["application/json"])
    fun getTaskByTaskHash(task_hash: String): Map<String, Any> {
        val r = scls.getTaskByTaskHash(task_hash)
        return mapOf(
            "task" to r,
            "message" to "Query done.",
            "code" to 200
        )
    }

    /**
     * Return all registered tasks by type
     * */
    @GetMapping("/tasks/allTasks/byType", produces = ["application/json"])
    fun getTasksByType(type: String): Map<String, Any> {
        return if (type != "cron" && type != "one" && type != "repeat") {
            mapOf(
                "tasks" to "none",
                "message" to "Unsupported type.",
                "code" to 403
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

    /**
     * Return all registered tasks by type, but "one" will return "one" and "repeat" jobs
     * */
    @GetMapping("/tasks/allTasks/byType/v2", produces = ["application/json"])
    fun getTasksByTypeV2(type: String): Map<String, Any> {
        return if (type != "cron" && type != "one" && type != "repeat") {
            mapOf(
                "tasks" to "none",
                "message" to "Unsupported type.",
                "code" to 403
            )
        } else {
            if (type == "one"){
                val r1 = scls.getAllTasksByType(type)
                val r2 = scls.getAllTasksByType("repeat")
                mapOf(
                    "tasks" to listOf(r1, r2).flatten(),
                    "message" to "Query done.",
                    "code" to 200
                )
            }else{
                val r = scls.getAllTasksByType(type)
                mapOf(
                    "tasks" to r,
                    "message" to "Query done.",
                    "code" to 200
                )
            }
        }
    }

    /**
     * Get all running tasks
     * */
    @GetMapping("/tasks/allRunning", produces = ["application/json"])
    fun getAllRunningTasks(): Map<String, Any> {
        return mapOf(
            "tasks" to slqs.getAllRunningTasks(),
            "message" to "Query done.",
            "code" to 200
        )
    }

    /**
     * Interrupt only cancel tasks has same TaskHash
     * */
    @PutMapping("/tasks/interrupt", produces = ["application/json"])
    fun interruptRunningJob(task_hash: String): Map<String, Any> {
        val currentRunningTaskHash = slqs.getAllRunningTasks().map { it.taskHash }.toList()
        return if (task_hash in currentRunningTaskHash) {
            cache.needToInterruptTasks.add(task_hash)
            mapOf(
                "message" to "Interrupt commit.",
                "code" to 200
            )
        } else {
            mapOf(
                "message" to "No such running task.",
                "code" to 403
            )
        }
    }

    /**
     * Halt will cancel all groups which contain running task with the same TaskHash
     * */
    @PutMapping("/tasks/halt", produces = ["application/json"])
    fun haltRunningGroup(task_hash: String): Map<String, Any> {
        val currentRunningTaskHash = slqs.getAllRunningTasks().map { it.taskHash }.toList()
        return if (task_hash in currentRunningTaskHash) {
            cache.needToHaltTasks.add(task_hash)
            mapOf(
                "message" to "Halt commit.",
                "code" to 200
            )
        } else {
            mapOf(
                "message" to "No such running task.",
                "code" to 403
            )
        }
    }

    /**
     * Get all logs' start_time and end_time and corresponds log_hash by TaskHash, need full timestamp, length is 13
     * */
    @GetMapping("/logs/brief", produces = ["application/json"])
    fun getLogsBrief(task_hash: String, st: Long, ed: Long): Map<String, Any> {
        val result = slqs.getLogsBriefByTaskHash(task_hash, st, ed)
        return mapOf(
            "brief" to result,
            "message" to "Query done.",
            "code" to 200
        )
    }

    @GetMapping("/logs/detail", produces = ["application/json"])
    fun getLogDetail(log_hash: String): Map<String, Any> {
        val result = slqs.getLogsByLogHash(log_hash)
        return mapOf(
            "log" to result,
            "message" to "Query done.",
            "code" to 200
        )
    }
}