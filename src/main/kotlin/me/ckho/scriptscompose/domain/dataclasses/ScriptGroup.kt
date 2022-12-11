package me.ckho.scriptscompose.domain.dataclasses

import me.ckho.scriptscompose.domain.ScriptGroupsCacheEntity

data class ScriptGroup(
    val cluster: String,
    val groupName: String,
    val jobType: String,
    val interval: Int,
    val commands: MutableList<ScriptArgSequence>,
    val workingDir: String,
    val startAt: String,
    val runWithTempBashScript: Boolean = false,
    val tmpBashWorkingDir: String = "/tmp/script_composer"
) {
    fun generateScriptGroupsEntity(): List<ScriptGroupsCacheEntity> {
        val result = mutableListOf<ScriptGroupsCacheEntity>()
        for (cmd in commands) {
            result.add(
                ScriptGroupsCacheEntity(
                    cluster,
                    groupName,
                    jobType,
                    interval,
                    cmd.generateCommandString(),
                    workingDir,
                    startAt,
                    runWithTempBashScript,
                    tmpBashWorkingDir
                )
            )
        }
        return result
    }

    companion object {
        fun allEntitiesInSameGroupToOneScriptGroup(entities: List<ScriptGroupsCacheEntity>): ScriptGroup {
            if (entities.map { it.groupName }.toSet().toList().size != 1) {
                throw AssertionError("Entities' group name not unique")
            }

            // if group names are same, only commands have differences between entities
            val baseObj = entities[0]
            val resultGroup = ScriptGroup(
                baseObj.cluster, baseObj.groupName, baseObj.jobType, baseObj.executeInterval,
                mutableListOf(), baseObj.workingDir, baseObj.startAt
            )

            for (e in entities) {
                resultGroup.commands.add(ScriptArgSequence(e.command.split(" ").toList()))
            }

            return resultGroup
        }
    }
}