package me.ckho.scriptscompose.domain

import java.sql.Clob
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "script_logs")
class ScriptLogsEntity(
    @Temporal(TemporalType.TIMESTAMP)
    var startTime: Date,
    @Temporal(TemporalType.TIMESTAMP)
    var endTime: Date,
    var jobGroup: String,
    var jobType: String,
    var jobInterval: Int,
    var jobCommand: String,
    // it means a group when to start
    var jobTrigger: String,
    var workingDir: String,
    @Lob
    var jobLogs: Clob,
    @Id
    @GeneratedValue
    var id: Long? = null
) {
    override fun toString(): String {
        return "ScriptLogsEntity(startTime=$startTime, endTime=$endTime, jobGroup='$jobGroup', jobType='$jobType', jobInterval=$jobInterval, jobCommand='$jobCommand', jobTrigger='$jobTrigger', workingDir='$workingDir', jobLogs=$jobLogs, id=$id)"
    }
}