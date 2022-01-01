package me.ckho.scriptscompose.service.impl

import me.ckho.scriptscompose.domain.dataclasses.ScriptLog
import me.ckho.scriptscompose.domain.enums.ScriptLogTaskStatus
import me.ckho.scriptscompose.repository.ScriptLogsRepository
import me.ckho.scriptscompose.utils.ResponseFactory
import me.ckho.scriptscompose.utils.timestamp2date
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ScriptLogsQueryService(
    @Autowired
    private val SLR: ScriptLogsRepository
) {
    fun getAllRunningTasks(): List<ScriptLog> {
        val r = SLR.findByTaskStatus(ScriptLogTaskStatus.RUNNING.desc)
        val result = mutableListOf<ScriptLog>()
        for (sle in r) {
            if (sle != null) {
                result.add(ResponseFactory.buildScriptLogsResponse(sle))
            }
        }
        return result
    }

    fun getLogsBriefByTaskHash(taskHash: String, st: Long, ed: Long): List<Map<String, String>> {
//        val r = SLR.findByTaskHash(taskHash)
        val r = SLR.findByTaskHashAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(taskHash, timestamp2date(st), timestamp2date(ed))
        val result = mutableListOf<Map<String, String>>()
        for (sle in r) {
            if (sle != null) {
                val tmp = mapOf(
                    "task_hash" to taskHash,
                    "duration" to "${sle.startTime} - ${sle.endTime}",
                    "log_hash" to sle.logHash,
                    "status" to sle.taskStatus
                )
                result.add(tmp)
            }
        }
        return result
    }

    fun getLogsByLogHash(logHash: String): String {
        val r = SLR.findByLogHash(logHash)
        return if (r != null) {
            r.jobLogs.getSubString(1, r.jobLogs.length().toInt())
        } else {
            "No log for this log_hash"
        }
    }
}