package me.ckho.scriptscompose.domain.dataclasses

import me.ckho.scriptscompose.domain.ScriptGroupsCacheEntity

data class ScriptComposeConfig(
    val scriptGroups: List<ScriptGroup>
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
}
