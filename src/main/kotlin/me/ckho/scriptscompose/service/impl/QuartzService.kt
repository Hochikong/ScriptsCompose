package me.ckho.scriptscompose.service.impl

import me.ckho.scriptscompose.domain.dataclasses.ScriptGroup
import me.ckho.scriptscompose.jobs.SimpleJob
import me.ckho.scriptscompose.utils.JSONMapper
import org.quartz.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class QuartzService(@Autowired val scheduler: Scheduler) {
    private val oneTimeDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun addOneTimeJob(scriptGroup: ScriptGroup) {
        val otj = buildJobDetail(scriptGroup, false)
        scheduler.scheduleJob(otj, buildOneTimeJobTrigger(otj, scriptGroup.start_at))
    }

    fun addCronJob(scriptGroup: ScriptGroup) {
        val otj = buildJobDetail(scriptGroup, true)
        scheduler.scheduleJob(otj, buildCronJobTrigger(otj, scriptGroup.start_at))
    }

    private fun buildJobDetail(
        scriptGroup: ScriptGroup,
        isCron: Boolean
    ): JobDetail {
        val groupName = scriptGroup.group_name
        val executeInterval = scriptGroup.interval

        val jobDataMap = JobDataMap()
        jobDataMap["group_name"] = scriptGroup.group_name
        jobDataMap["job_type"] = scriptGroup.job_type
        jobDataMap["interval"] = scriptGroup.interval
        jobDataMap["commands"] = scriptGroup.commands
        jobDataMap["working_dir"] = scriptGroup.working_dir
        jobDataMap["start_at"] = scriptGroup.start_at
        jobDataMap["configJSON"] = JSONMapper.writeValueAsString(scriptGroup)

        // The minimum interval is 500 millis, 0.5 seconds
        if (executeInterval > 0) {
            jobDataMap["interval"] = executeInterval * 1000
        } else {
            jobDataMap["interval"] = 500
        }

        val desc: String = if (isCron){
            "A cron job"
        }else{
            "A simple one time job"
        }

        return JobBuilder.newJob(SimpleJob::class.java)
            .withIdentity(UUID.randomUUID().toString(), groupName)
            .withDescription(desc)
            .usingJobData(jobDataMap)
            .build()
    }


    private fun buildOneTimeJobTrigger(jobDetail: JobDetail, startAt: String): Trigger {
        val group: String = jobDetail.jobDataMap["group_name"] as String

        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.key.name, "${group}_triggers")
            .withDescription("One time job $group Execute Trigger")
            .startAt(oneTimeDateFormat.parse(startAt))
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
            .build()
    }

    private fun buildCronJobTrigger(jobDetail: JobDetail, startAt: String): Trigger {
        val group: String = jobDetail.jobDataMap["group_name"] as String

        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.key.name, "${group}_triggers")
            .withDescription("Cron job $group Execute Trigger")
            .withSchedule(CronScheduleBuilder.cronSchedule(startAt).withMisfireHandlingInstructionFireAndProceed())
            .build()
    }
}