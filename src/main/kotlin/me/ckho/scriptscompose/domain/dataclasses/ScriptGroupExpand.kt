package me.ckho.scriptscompose.domain.dataclasses

data class ScriptGroupExpand(
    val cluster: String,
    val group_name: String,
    val job_type: String,
    val interval: Int,
    val command: String,
    val working_dir: String,
    val start_at: String,
    val task_hash: String,
    val runWithTempBashScript: Boolean,
    val tmpBashWorkingDir: String
)