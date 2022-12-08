package me.ckho.scriptscompose.domain.dataclasses

import me.ckho.scriptscompose.domain.ScriptGroupsCacheEntity

data class ScriptGroup(
    val cluster: String,
    val groupName: String,
    val jobType: String,
    val interval: Int,
    val commands: List<ScriptArgSequence>,
    val workingDir: String,
    val startAt: String
){
    fun generateScriptGroupsEntity(): List<ScriptGroupsCacheEntity>{
        val result = mutableListOf<ScriptGroupsCacheEntity>()
        for (cmd in commands){
            result.add(
                ScriptGroupsCacheEntity(cluster,groupName,jobType,interval,cmd.generateCommandString(),workingDir,startAt)
            )
        }
        return result
    }
}