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
    val cluster: String,
    var jobGroup: String,
    var jobType: String,
    var jobInterval: Int,
    var jobCommand: String,
    // it means a group when to start
    var jobTrigger: String,
    var workingDir: String,
    var logHash: String,
    var taskHash: String,
    var taskStatus: String,
    @Lob
    var jobLogs: Clob,
    val runWithTempBashScript: Boolean = false,
    val tmpBashWorkingDir: String = "/tmp/script_composer",
    @Id
    @GeneratedValue
    var id: Long? = null
) {
    override fun toString(): String {
        return "ScriptLogsEntity(startTime=$startTime, endTime=$endTime, cluster='$cluster', jobGroup='$jobGroup', jobType='$jobType', jobInterval=$jobInterval, jobCommand='$jobCommand', jobTrigger='$jobTrigger', workingDir='$workingDir', logHash='$logHash', taskHash='$taskHash', taskStatus='$taskStatus', jobLogs=$jobLogs, runWithTempBashScript=$runWithTempBashScript, tmpBashWorkingDir='$tmpBashWorkingDir', id=$id)"
    }
}