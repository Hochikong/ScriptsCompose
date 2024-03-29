package me.ckho.scriptscompose.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import me.ckho.scriptscompose.domain.dataclasses.ScriptComposeConfig
import me.ckho.scriptscompose.domain.dataclasses.ScriptGroup
import me.ckho.scriptscompose.domain.dataclasses.ScriptGroupExpand
import me.ckho.scriptscompose.repository.ScriptGroupsCacheRepository
import me.ckho.scriptscompose.utils.ResponseFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class ScriptsConfigLoaderService(
    @Autowired
    val slCache: ScriptGroupsCacheRepository,
    @Autowired
    val lowLevelDBOps: LowLevelDBOps
) {
    private lateinit var scg: ScriptComposeConfig

    private val mapper = ObjectMapper(YAMLFactory()).apply { findAndRegisterModules() }.apply {
        propertyNamingStrategy =
            PropertyNamingStrategies.SNAKE_CASE
    }

    /**
     * Load scripts_register.yaml from default location.
     * */
    fun loadConfigs(): ScriptComposeConfig {
        scg = mapper.readValue(File("./config/scripts_register.yaml"), ScriptComposeConfig::class.java)
        return scg
    }

    fun updateScriptComposeConfig(scg: ScriptComposeConfig) {
        this.scg = scg
    }

    fun getSCG(): ScriptComposeConfig? {
        return scg
    }

    fun addOneTimeScriptGroupToSCG(task: ScriptGroup) {
        this.scg.scriptGroups.add(task)
    }

    fun getAllGroupsFromSCG(): List<String> {
        return scg.scriptGroups.map { it.groupName }.toList()
    }

    fun getGroupsByType(type: String): List<String> {
        return scg.scriptGroups.filter { it.jobType == type }.map { it.cluster }.toList()
    }

    /**
     * Only get registered tasks' detail, not with script logs
     * */
    fun getAllTasksFromSCG(): List<ScriptGroupExpand> {
        val result = mutableListOf<ScriptGroupExpand>()
        for (sg in scg.scriptGroups) {
            val tmp = ResponseFactory.buildScriptGroupExpandResponse(sg)
            tmp.map { result.add(it) }
        }
        return result
    }

    /**
     * Only get registered tasks' detail by job type, not with script logs
     * */
    fun getAllTasksByType(type: String): List<ScriptGroupExpand> {
        val result = mutableListOf<ScriptGroupExpand>()
        for (sg in scg.scriptGroups) {
            if (sg.jobType == type) {
                val tmp = ResponseFactory.buildScriptGroupExpandResponse(sg)
                tmp.map { result.add(it) }
            }
        }
        return result
    }

    fun getTaskByTaskHash(taskHash: String): ScriptGroupExpand {
        val result = mutableListOf<ScriptGroupExpand>()
        for (sg in scg.scriptGroups) {
            val tmp = ResponseFactory.buildScriptGroupExpandResponse(sg)
            tmp.map { result.add(it) }
        }
        return result.filter { it.task_hash == taskHash }.toList()[0]
    }

    /**
     * Load your own script register configuration file from specific path.
     * @param path: Your own config file path, yaml format.
     * */
    fun loadConfigsFrom(path: String): ScriptComposeConfig {
        return mapper.readValue(File(path), ScriptComposeConfig::class.java)
    }

    /* Script Groups Cache */
    fun reloadFileAndRefreshCache() {
        lowLevelDBOps.recreateScriptGroupsCacheTable()
        val newRecords = this.scg.generateForRefreshCache()
        for (r in newRecords) {
            slCache.save(r)
        }
    }
}


