package me.ckho.scriptscompose.service.impl

import me.ckho.scriptscompose.jobs.SimpleJob
import org.quartz.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class QuartzService(@Autowired val scheduler: Scheduler) {
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun addJob(startAt: String){
        val jt = buildJobDetail()
        scheduler.scheduleJob(jt, buildJobTrigger(jt, startAt))
    }

    fun buildJobDetail(): JobDetail {
        val jobDataMap = JobDataMap()
        jobDataMap["script"] = listOf("python", "sycm_compose.py", "-c 1.txt").toTypedArray()
        return JobBuilder.newJob(SimpleJob::class.java)
            .withIdentity(UUID.randomUUID().toString(), "SN1")
            .withDescription("Execute python script")
            .usingJobData(jobDataMap)
            .build()
    }


    fun buildJobTrigger(jobDetail: JobDetail, startAt: String): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.key.name, "SN1_triggers")
            .withDescription("SN1 Execute Trigger")
            .startAt(sdf.parse(startAt))
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
            .build()
    }
}