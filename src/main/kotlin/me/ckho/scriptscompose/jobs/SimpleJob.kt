package me.ckho.scriptscompose.jobs

import me.ckho.scriptscompose.service.ScriptExecutorService
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SimpleJob(
    @Autowired val executor: ScriptExecutorService
) : Job{
    var logger = LoggerFactory.getLogger(SimpleJob::class.java)


    override fun execute(context: JobExecutionContext) {
        val r = executor.runShellCommand(listOf("python", "sycm_compose.py", "-c 1.txt"), "./")
        logger.info(r.toString())
    }
}