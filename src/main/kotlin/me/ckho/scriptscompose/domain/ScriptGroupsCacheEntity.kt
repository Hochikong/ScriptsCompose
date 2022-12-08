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
    @Id
    @GeneratedValue
    var id: Long? = null
) {
    override fun toString(): String {
        return "ScriptGroupsCacheEntity(cluster='$cluster', groupName='$groupName', jobType='$jobType', interval=$executeInterval, command='$command', workingDir='$workingDir', startAt='$startAt', id=$id)"
    }
}