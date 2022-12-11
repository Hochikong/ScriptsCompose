package me.ckho.scriptscompose.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "script_groups_cache")
class ScriptGroupsCacheEntity(
    var cluster: String,
    var groupName: String,
    var jobType: String,
    var executeInterval: Int,
    var command: String,
    var workingDir: String,
    var startAt: String,
    val runWithTempBashScript: Boolean,
    val tmpBashWorkingDir: String,
    @Id
    @GeneratedValue
    var id: Long? = null
) {
    override fun toString(): String {
        return "ScriptGroupsCacheEntity(cluster='$cluster', groupName='$groupName', jobType='$jobType', executeInterval=$executeInterval, command='$command', workingDir='$workingDir', startAt='$startAt', runWithTempBashScript=$runWithTempBashScript, tmpBashWorkingDir='$tmpBashWorkingDir', id=$id)"
    }
}