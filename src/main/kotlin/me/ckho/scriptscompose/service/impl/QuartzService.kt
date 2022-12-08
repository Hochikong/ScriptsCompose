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
        scheduler.scheduleJob(otj, buildOneTimeJobTrigger(otj, scriptGroup.startAt))
    }

    fun addCronJob(scriptGroup: ScriptGroup) {
        val otj = buildJobDetail(scriptGroup, true)
        scheduler.scheduleJob(otj, buildCronJobTrigger(otj, scriptGroup.startAt))
    }

    private fun buildJobDetail(
        scriptGroup: ScriptGroup,
        isCron: Boolean
    ): JobDetail {
        val groupName = scriptGroup.groupName
        val executeInterval = scriptGroup.interval

        val jobDataMap = JobDataMap()
        jobDataMap["group_name"] = scriptGroup.groupName
        jobDataMap["job_type"] = scriptGroup.jobType
        jobDataMap["interval"] = scriptGroup.interval
        jobDataMap["commands"] = scriptGroup.commands
        jobDataMap["working_dir"] = scriptGroup.workingDir
        jobDataMap["start_at"] = scriptGroup.startAt
        jobDataMap["configJSON"] = JSONMapper.writeValueAsString(scriptGroup)

        // The minimum interval is 500 millis, 0.5 seconds
        if (executeInterval > 0) {
            jobDataMap["interval"] = executeInterval * 1000
        } else {
            jobDataMap["interval"] = 500
        }

        val desc: String = if (isCron) {
            "A cron job"
        } else {
            "A simple one time job"
        }

        val key = UUID.randomUUID().toString()
        return JobBuilder.newJob(SimpleJob::class.java)
            .withIdentity(key, groupName)
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