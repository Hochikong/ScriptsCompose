package me.ckho.scriptscompose

import me.ckho.scriptscompose.domain.dataclasses.ScriptComposeConfig
import me.ckho.scriptscompose.domain.enums.JobTypes
import me.ckho.scriptscompose.repository.ScriptGroupsCacheRepository
import me.ckho.scriptscompose.service.impl.QuartzService
import me.ckho.scriptscompose.service.impl.ScriptsConfigLoaderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.system.exitProcess


@SpringBootApplication
class MainApp(
    @Autowired val SGCR: ScriptGroupsCacheRepository,
    @Autowired val qz: QuartzService,
    @Autowired val cfg: ScriptsConfigLoaderService
) : CommandLineRunner {
    private var logger: Logger = LoggerFactory.getLogger(MainApp::class.java)

    override fun run(vararg args: String?) {
//        args.forEach { println(it) }
        var sc: ScriptComposeConfig? = null
        try{
            when(args[0] ?: ""){

                "-C" -> {
                    try {
                        val records = SGCR.findAll()

                    }catch (e: Exception){
                        println("Error happened: $e")
                    }
                }
                "-L" -> {
                    try {
                        sc = cfg.loadConfigs()
                        cfg.reloadFileAndRefreshCache()
                    }catch (e: Exception){
                        println("Error happened: $e")
                    }
                }
                "-h" -> {
                    println("""
                    
                    
                    Supported Launch Arguments:
                    1. -h    Get some help
                    2. -L    Load tasks from config file and rebuild SCRIPT_GROUPS_CACHE table.
                    2. -C    Try to load tasks from SCRIPT_GROUPS_CACHE table, if failed, load from config file and rebuild cache. 
                    
                    
                    
                """.trimIndent())
                    exitProcess(0)
                }
                else -> {
                    try {
                        sc = cfg.loadConfigs()
                        cfg.reloadFileAndRefreshCache()
                    }catch (e: Exception){
                        println("Error happened: $e")
                    }
                }
            }
        }catch (e: ArrayIndexOutOfBoundsException){
            try {
                sc = cfg.loadConfigs()
                cfg.reloadFileAndRefreshCache()
            }catch (e: Exception){
                println("Error happened: $e")
            }
        }

        // load task and init script_groups_cache table


        //

        if (sc != null) {
            for (scg in sc.scriptGroups){
                when (scg.jobType) {
                    JobTypes.OneTime.t -> {
                        qz.addOneTimeJob(scg)
                        logger.info("Add one-time job group ${scg.groupName}")
                    }
                    JobTypes.Repeat.t -> {
                        qz.addOneTimeJob(scg)
                        logger.info("Add repeat one-time job group ${scg.groupName}")
                    }
                    JobTypes.Cron.t -> {
                        qz.addCronJob(scg)
                        logger.info("Add cron job group ${scg.groupName}")
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(MainApp::class.java, *args)
}