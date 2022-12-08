package me.ckho.scriptscompose

import me.ckho.scriptscompose.domain.dataclasses.ScriptComposeConfig
import me.ckho.scriptscompose.domain.enums.JobTypes
import me.ckho.scriptscompose.service.impl.QuartzService
import me.ckho.scriptscompose.service.impl.ScriptsConfigLoaderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class MainApp(
    @Autowired val qz: QuartzService,
    @Autowired val cfg: ScriptsConfigLoaderService
) : CommandLineRunner {
    private var logger: Logger = LoggerFactory.getLogger(MainApp::class.java)

    override fun run(vararg args: String?) {
        // init
//        println("Add quartz job ...")
//        qz.addOneTimeJob(
//            ScriptGroup(
//                "SN1",
//                "one",
//                listOf(ScriptArgSequence(command_arg_seq = listOf("python", "sycm_compose.py", "-c 1.txt"))),
//                "./",
//                "2021-11-21 04:06:00"
//            )
//        )
        var sc: ScriptComposeConfig? = null
        try {
            sc = cfg.loadConfigs()
            cfg.reloadFileAndRefreshCache()
        }catch (e: Exception){
            println("Error happened")
        }

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