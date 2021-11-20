package me.ckho.scriptscompose.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import me.ckho.scriptscompose.domain.dataclasses.ScriptComposeConfig
import org.springframework.stereotype.Service
import java.io.File

@Service
class ScriptsConfigLoaderService {
    private val mapper = ObjectMapper(YAMLFactory()).apply { findAndRegisterModules() }

    /**
     * Load scripts_register.yaml from default location.
     * */
    fun loadConfigs(){
        val scc = mapper.readValue(File("./config/scripts_register.yaml"), ScriptComposeConfig::class.java)
        println(scc)
    }

    /**
     * Load your own script register configuration file from specific path.
     * @param path: Your own config file path, yaml format.
     * */
    fun loadConfigsFrom(path: String){
        val scc = mapper.readValue(File(path), ScriptComposeConfig::class.java)
        println(scc)
    }
}