package me.ckho.scriptscompose.domain.dataclasses

import java.util.*


data class ScriptLog(
    var startTime: Date,
    var endTime: Date,
    var cluster: String,
    var jobGroup: String,
    var jobType: String,
    var jobInterval: Int,
    var jobCommand: String,
    var jobTrigger: String,
    var workingDir: String,
    var logHash: String,
    var taskHash: String,
    var taskStatus: String,
    var jobLogs: String
)