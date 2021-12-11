package me.ckho.scriptscompose.jobs

import me.ckho.scriptscompose.domain.dataclasses.ScriptArgSequence
import me.ckho.scriptscompose.service.impl.ScriptExecutorService
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SimpleJob(
    @Autowired val executor: ScriptExecutorService
) : InterruptableJob{
    private var logger: Logger = LoggerFactory.getLogger(SimpleJob::class.java)

    override fun execute(context: JobExecutionContext) {
        executor.start()
        logger.info(executor.uid)
        val jdm = context.jobDetail.jobDataMap
        executor.updateExecuteInterval((jdm["interval"] as Int).toLong())
        executor.loadScriptGroupAndRegisterScriptLogs(jdm["configJSON"] as String)
        val jobType = jdm["job_type"]
        val script: List<ScriptArgSequence> = jdm["commands"] as List<ScriptArgSequence>
        val wd = jdm["working_dir"] as String

        for (s in script){
            executor.commitShellCommand(s.command_arg_seq, wd, jobType as String)
        }
    }

    override fun interrupt() {
        executor.stop()
    }
}