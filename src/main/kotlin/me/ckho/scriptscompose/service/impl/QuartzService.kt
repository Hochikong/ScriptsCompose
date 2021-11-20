package me.ckho.scriptscompose.service.impl

import me.ckho.scriptscompose.domain.dataclasses.ScriptArgSequence
import me.ckho.scriptscompose.domain.dataclasses.ScriptGroup
import me.ckho.scriptscompose.jobs.SimpleJob
import org.quartz.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class QuartzService(@Autowired val scheduler: Scheduler) {
    private val oneTimeDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun addOneTimeJob(scriptGroup: ScriptGroup){
        val otj = buildOneTimeJobDetail(scriptGroup.group_name, scriptGroup.commands, scriptGroup.working_dir)
        scheduler.scheduleJob(otj, buildOneTimeJobTrigger(otj, scriptGroup.start_at))
    }

    private fun buildOneTimeJobDetail(groupName: String, scripts: List<ScriptArgSequence>, working_dir: String): JobDetail {
        val jobDataMap = JobDataMap()
        jobDataMap["scripts"] = scripts
        jobDataMap["working_dir"] = working_dir
        jobDataMap["group"] = groupName
        return JobBuilder.newJob(SimpleJob::class.java)
            .withIdentity(UUID.randomUUID().toString(), groupName)
            .withDescription("A simple one time job")
            .usingJobData(jobDataMap)
            .build()
    }


    private fun buildOneTimeJobTrigger(jobDetail: JobDetail, startAt: String): Trigger {
        val group: String = jobDetail.jobDataMap["group"] as String

        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.key.name, "${group}_triggers")
            .withDescription("One time job $group Execute Trigger")
            .startAt(oneTimeDateFormat.parse(startAt))
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
            .build()
    }
}