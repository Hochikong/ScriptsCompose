package me.ckho.scriptscompose.utils

import me.ckho.scriptscompose.domain.dataclasses.ScriptGroup
import me.ckho.scriptscompose.domain.dataclasses.ScriptGroupExpand

object ResponseFactory {
    fun buildScriptGroupExpandResponse(scg: ScriptGroup): List<ScriptGroupExpand> {
        return scg.commands.map {
            ScriptGroupExpand(
                group_name = scg.group_name,
                job_type = scg.job_type,
                interval = scg.interval,
                command = it.command_arg_seq.reduce { acc, s -> "$acc $s" },
                working_dir = scg.working_dir,
                start_at = scg.start_at
            )
        }.toList()
    }
}