package me.ckho.scriptscompose.domain.dataclasses


data class ScriptLog(
    var startTime: String,
    var endTime: String,
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