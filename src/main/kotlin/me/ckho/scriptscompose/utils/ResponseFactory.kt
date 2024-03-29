package me.ckho.scriptscompose.utils

import me.ckho.scriptscompose.domain.ScriptLogsEntity
import me.ckho.scriptscompose.domain.dataclasses.ScriptGroup
import me.ckho.scriptscompose.domain.dataclasses.ScriptGroupExpand
import me.ckho.scriptscompose.domain.dataclasses.ScriptLog

object ResponseFactory {
    fun buildScriptGroupExpandResponse(scg: ScriptGroup): List<ScriptGroupExpand> {
        return scg.commands.map {
            ScriptGroupExpand(
                cluster = scg.cluster,
                group_name = scg.groupName,
                job_type = scg.jobType,
                interval = scg.interval,
                command = it.commandArgSeq.reduce { acc, s -> "$acc $s" },
                working_dir = scg.workingDir,
                start_at = scg.startAt,
                task_hash = generateUIDForTask("${scg.groupName} ${scg.jobType} ${scg.interval} ${it.commandArgSeq.reduce { acc, s -> "$acc $s" }} ${scg.workingDir}"),
                runWithTempBashScript = scg.runWithTempBashScript,
                tmpBashWorkingDir = scg.tmpBashWorkingDir
            )
        }.toList()
    }

    fun buildScriptLogsResponse(sle: ScriptLogsEntity): ScriptLog {
        return ScriptLog(
            startTime = convertToLocalDateTimeViaInstant(sle.startTime).toString().replace("T", " "),
            endTime = convertToLocalDateTimeViaInstant(sle.endTime).toString().replace("T", " "),
            cluster = sle.cluster,
            jobGroup = sle.jobGroup,
            jobType = sle.jobType,
            jobInterval = sle.jobInterval,
            jobCommand = sle.jobCommand,
            jobTrigger = sle.jobTrigger,
            workingDir = sle.workingDir,
            logHash = sle.logHash,
            taskHash = sle.taskHash,
            taskStatus = sle.taskStatus,
            jobLogs = "No Showing Here",
//            jobLogs = sle.jobLogs.getSubString(1, sle.jobLogs.length().toInt())
            runWithTempBashScript = sle.runWithTempBashScript,
            tmpBashWorkingDir = sle.tmpBashWorkingDir
        )
    }
}