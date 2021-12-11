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
                start_at = scg.start_at
            )
        }.toList()
    }

    fun buildScriptLogsResponse(sle: ScriptLogsEntity): ScriptLog {
        return ScriptLog(
            startTime = sle.startTime,
            endTime = sle.endTime,
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
            jobLogs = sle.jobLogs.getSubString(1, sle.jobLogs.length().toInt())
        )
    }
}