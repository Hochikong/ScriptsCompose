package me.ckho.scriptscompose.domain.dataclasses

data class ScriptGroupExpand(
    val group_name: String,
    val job_type: String,
    val interval: Int,
    val command: String,
    val working_dir: String,
    val start_at: String
)