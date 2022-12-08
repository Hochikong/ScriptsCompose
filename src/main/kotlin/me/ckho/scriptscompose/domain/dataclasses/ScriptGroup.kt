package me.ckho.scriptscompose.domain.dataclasses

data class ScriptGroup(
    val cluster: String,
    val groupName: String,
    val jobType: String,
    val interval: Int,
    val commands: List<ScriptArgSequence>,
    val workingDir: String,
    val startAt: String
)