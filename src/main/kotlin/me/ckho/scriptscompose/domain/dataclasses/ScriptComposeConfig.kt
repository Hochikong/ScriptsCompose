package me.ckho.scriptscompose.domain.dataclasses

import me.ckho.scriptscompose.domain.ScriptGroupsCacheEntity

data class ScriptComposeConfig(
    val scriptGroups: MutableList<ScriptGroup>
){
    fun generateForRefreshCache(): List<ScriptGroupsCacheEntity>{
        val result = mutableListOf<ScriptGroupsCacheEntity>()
        for (sg in scriptGroups){
            val tmp = sg.generateScriptGroupsEntity()
            for (t in tmp){
                result.add(t)
            }
        }
        return result
    }

    companion object{
        fun allEntitiesSameGroupToScriptGroups(entities: List<ScriptGroupsCacheEntity>): ScriptComposeConfig{
            val result = ScriptComposeConfig(mutableListOf())
            val distinctGroupNames = entities.map { it.groupName }.toSet().toList()
            for (name in distinctGroupNames){
                val groups = ScriptGroup.allEntitiesInSameGroupToOneScriptGroup(entities.filter { it.groupName == name }.toList())
                result.scriptGroups.add(groups)
            }
            return result
        }
    }
}
