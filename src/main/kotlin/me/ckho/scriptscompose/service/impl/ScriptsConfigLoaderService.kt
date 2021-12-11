package me.ckho.scriptscompose.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import me.ckho.scriptscompose.domain.dataclasses.ScriptComposeConfig
import me.ckho.scriptscompose.domain.dataclasses.ScriptGroupExpand
import me.ckho.scriptscompose.domain.dataclasses.ScriptLog
import me.ckho.scriptscompose.domain.enums.ScriptLogTaskStatus
import me.ckho.scriptscompose.repository.ScriptLogsRepository
import me.ckho.scriptscompose.utils.ResponseFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class ScriptsConfigLoaderService(
    @Autowired
    private val SLR: ScriptLogsRepository
) {
    private lateinit var scg: ScriptComposeConfig

    private val mapper = ObjectMapper(YAMLFactory()).apply { findAndRegisterModules() }

    /**
     * Load scripts_register.yaml from default location.
     * */
    fun loadConfigs(): ScriptComposeConfig {
        scg = mapper.readValue(File("./config/scripts_register.yaml"), ScriptComposeConfig::class.java)
        return scg
    }

    fun getSCG(): ScriptComposeConfig? {
        return scg
    }

    fun getAllGroupsFromSCG(): List<String> {
        return scg.script_groups.map { it.group_name }.toList()
    }

    fun getGroupsByType(type: String): List<String> {
        return scg.script_groups.filter { it.job_type == type }.map { it.cluster }.toList()
    }

    fun getAllTasksFromSCG(): List<ScriptGroupExpand> {
        val result = mutableListOf<ScriptGroupExpand>()
        for (sg in scg.script_groups) {
            val tmp = ResponseFactory.buildScriptGroupExpandResponse(sg)
            tmp.map { result.add(it) }
        }
        return result
    }

    fun getAllTasksByType(type: String): List<ScriptGroupExpand> {
        val result = mutableListOf<ScriptGroupExpand>()
        for (sg in scg.script_groups) {
            if (sg.job_type == type) {
                val tmp = ResponseFactory.buildScriptGroupExpandResponse(sg)
                tmp.map { result.add(it) }
            }
        }
        return result
    }

    fun getAllRunningTasks(): List<ScriptLog> {
        val r = SLR.findByTaskStatus(ScriptLogTaskStatus.RUNNING.desc)
        val result = mutableListOf<ScriptLog>()
        for (sle in r) {
            if (sle != null) {
                result.add(ResponseFactory.buildScriptLogsResponse(sle))
            }
        }
        return result
    }

    /**
     * Load your own script register configuration file from specific path.
     * @param path: Your own config file path, yaml format.
     * */
    fun loadConfigsFrom(path: String): ScriptComposeConfig {
        return mapper.readValue(File(path), ScriptComposeConfig::class.java)
    }
}