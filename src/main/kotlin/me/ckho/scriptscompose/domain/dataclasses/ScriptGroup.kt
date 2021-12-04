package me.ckho.scriptscompose.domain.dataclasses

data class ScriptGroup(
    val group_name: String,
    val job_type: String,
    val interval: Int,
    val commands: List<ScriptArgSequence>,
    val working_dir: String,
    val start_at: String
)