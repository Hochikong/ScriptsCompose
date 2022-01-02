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
                group_name = scg.group_name,
                job_type = scg.job_type,
                interval = scg.interval,
                command = it.command_arg_seq.reduce { acc, s -> "$acc $s" },
                working_dir = scg.working_dir,
                start_at = scg.start_at,
                task_hash = generateUIDForTask("${scg.group_name} ${scg.job_type} ${scg.interval} ${it.command_arg_seq.reduce { acc, s -> "$acc $s" }} ${scg.working_dir}")
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
            jobLogs = "No Showing Here"
//            jobLogs = sle.jobLogs.getSubString(1, sle.jobLogs.length().toInt())
        )
    }
}