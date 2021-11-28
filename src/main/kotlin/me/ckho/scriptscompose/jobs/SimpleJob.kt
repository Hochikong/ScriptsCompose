package me.ckho.scriptscompose.jobs

import me.ckho.scriptscompose.domain.dataclasses.ScriptArgSequence
import me.ckho.scriptscompose.service.impl.ScriptExecutorService
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SimpleJob(
//    @Autowired val executor: ScriptExecutorService
) : InterruptableJob{
    val executor = ScriptExecutorService()
    private var logger: Logger = LoggerFactory.getLogger(SimpleJob::class.java)

    override fun execute(context: JobExecutionContext) {
        executor.start()
        logger.info(executor.uid)
        val jdm = context.jobDetail.jobDataMap
        val script: List<ScriptArgSequence> = jdm["scripts"] as List<ScriptArgSequence>
        val wd = jdm["working_dir"] as String

        for (s in script){
            executor.commitShellCommand(s.command_arg_seq, wd)
        }
    }

    override fun interrupt() {
        executor.stop()
    }
}