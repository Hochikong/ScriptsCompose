package me.ckho.scriptscompose.service.impl

import me.ckho.scriptscompose.domain.ScriptLogsEntity
import me.ckho.scriptscompose.domain.dataclasses.ScriptGroup
import me.ckho.scriptscompose.domain.enums.JobTypes
import me.ckho.scriptscompose.domain.enums.ScriptLogTaskStatus
import me.ckho.scriptscompose.repository.ScriptLogsRepository
import me.ckho.scriptscompose.utils.JSONMapper
import me.ckho.scriptscompose.utils.generateUIDForTask
import me.ckho.scriptscompose.utils.string2instant
import org.hibernate.engine.jdbc.ClobProxy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.thread


@Service
@Scope(value = "prototype")
class ScriptExecutorService(
    @Autowired
    private val SLR: ScriptLogsRepository
) {
    private var logger: Logger = LoggerFactory.getLogger(ScriptExecutorService::class.java)
    private val stdDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private var commands: MutableList<Array<String>> = mutableListOf()
    private var workingDirs: MutableList<String> = mutableListOf()
    private var stopThreadsFlag = false
    private var taskRunningFlag = false
    private var stdoutAccumulate: String = "** STDOUT **\n"
    private var stderrAccumulate: String = "** STDERR **\n"
    private lateinit var proc: Process
    private var executeInterval: Long = 500L
    private lateinit var scg: ScriptGroup
    var uid = "Script Executor Service -> ${UUID.randomUUID()}"
    private var currentTaskUUID = ""
    private var currentJobType = ""

    fun getOutput(): Array<String> {
        return arrayOf(stdoutAccumulate, stderrAccumulate)
    }

    fun loadScriptGroupAndRegisterScriptLogs(sg: String) {
        scg = JSONMapper.readValue(sg, ScriptGroup::class.java)
    }

    fun updateExecuteInterval(seconds: Long) {
        executeInterval = seconds
    }

    fun commitShellCommand(command: List<String>, workingDir: String, jobType: String) {
        currentJobType = jobType
        commands.add(command.toTypedArray())
        workingDirs.add(workingDir)
    }

    fun runShellCommand(command: Array<String>, workingDir: String) {
        // reset
        stdoutAccumulate = "** STDOUT **\n"
        stderrAccumulate = "** STDERR **\n"

        logger.info("### Run a command ###")
        val rt = Runtime.getRuntime()
        taskRunningFlag = true
        val start = getCurrentDatetimeAsDate()


        val tmpTaskHash = UUID.randomUUID().toString()
        currentTaskUUID = tmpTaskHash

        val r = SLR.save(
            ScriptLogsEntity(
                startTime = start,
                endTime = Date.from(string2instant("1970-01-01 00:00:00")),
                cluster = scg.cluster,
                jobGroup = scg.group_name,
                jobType = scg.job_type,
                jobInterval = scg.interval,
                jobCommand = command.reduce { acc, s -> acc + s },
                jobTrigger = scg.start_at,
                workingDir = scg.working_dir,
                logHash = "",
                taskHash = tmpTaskHash,
                taskStatus = ScriptLogTaskStatus.RUNNING.desc,
                jobLogs = ClobProxy.generateProxy(stdoutAccumulate + stderrAccumulate)
            )
        )
        val jobID = r.id

        File("A.txt").writeText(r.toString())

        proc = rt.exec(command, null, File(workingDir))
        proc.waitFor()
        val end = getCurrentDatetimeAsDate()
        taskRunningFlag = false
        logger.info("### Run end ###")

        val logHash = when (scg.job_type) {
            "one" -> {
                generateUIDForTask(
                    """
                    ${scg.group_name} ${scg.job_type} ${scg.interval} 
                    ${command.reduce { acc, s -> acc + s }} ${scg.working_dir} one
                """.trimIndent()
                )
            }
            "cron" -> {
                generateUIDForTask(
                    """
                    ${scg.group_name} ${scg.job_type} ${scg.interval} 
                    ${command.reduce { acc, s -> acc + s }} ${scg.working_dir} cron ${scg.start_at}
                """.trimIndent()
                )
            }
            "repeat" -> {
                generateUIDForTask(
                    """
                    ${scg.group_name} ${scg.job_type} ${scg.interval} 
                    ${command.reduce { acc, s -> acc + s }} ${scg.working_dir} repeat ${scg.start_at}
                """.trimIndent()
                )
            }
            else -> {
                "INVALID JOB_TYPE"
            }
        }

        val taskHash = generateUIDForTask(
            """
            ${scg.group_name} ${scg.job_type} ${scg.interval} ${command.reduce { acc, s -> acc + s }} ${scg.working_dir}
        """.trimIndent()
        )

        // update
        val sLER = SLR.findById(jobID!!.toLong()).get()
        sLER.endTime = end
        sLER.logHash = logHash
        sLER.taskHash = taskHash
        sLER.taskStatus = ScriptLogTaskStatus.FINISHED.desc
        sLER.jobLogs = ClobProxy.generateProxy(stdoutAccumulate + stderrAccumulate)

        File("B.txt").writeText(sLER.toString())

        SLR.save(sLER)
//        SLR.save(
//            ScriptLogsEntity(
//                startTime = start,
//                endTime = end,
//                jobGroup = scg.group_name,
//                jobType = scg.job_type,
//                jobInterval = scg.interval,
//                jobCommand = command.reduce { acc, s -> acc + s },
//                jobTrigger = scg.start_at,
//                workingDir = scg.working_dir,
//                logHash = logHash,
//                taskHash = taskHash,
//                taskStatus = ScriptLogTaskStatus.FINISHED.desc,
//                jobLogs = ClobProxy.generateProxy(stdoutAccumulate + stderrAccumulate)
//            )
//        )
    }

    private fun getCurrentDatetime(): String {
        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val now = LocalDateTime.now()
        return dtf.format(now)
    }

    private fun getCurrentDatetimeAsDate(): Date {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
    }

    fun start() {
        thread {
            while (!stopThreadsFlag) {
                if (taskRunningFlag) {
                    try {
                        val stdInput = BufferedReader(InputStreamReader(proc.inputStream))
                        var si: String?
                        while (stdInput.readLine().also {
                                si = it
                                if (it != null) {
                                    stdoutAccumulate += "${getCurrentDatetime()}  $it\n"
                                }
                            } != null) {
                            logger.info(si)
                        }
                    } catch (_: Exception) {
                    }
                }
                Thread.sleep(500)
            }
        }

        thread {
            while (!stopThreadsFlag) {
                if (taskRunningFlag) {
                    try {
                        val stdError = BufferedReader(InputStreamReader(proc.errorStream))
                        var se: String?
                        while (stdError.readLine().also {
                                se = it
                                if (it != null) {
                                    stderrAccumulate += "${getCurrentDatetime()}  $it\n"
                                }
                            } != null) {
                            logger.info(se)
                        }
                    } catch (_: Exception) {
                    }
                }
                Thread.sleep(500)
            }
        }

        thread {
            while (!stopThreadsFlag) {
                if (commands.size > 0) {
                    if (currentJobType != JobTypes.Repeat.t) {
                        val c = commands[0]
                        val wd = workingDirs[0]
                        runShellCommand(c, wd)
                        commands.removeAt(0)
                        workingDirs.removeAt(0)
                    } else {
                        val c = commands[0]
                        val wd = workingDirs[0]
                        runShellCommand(c, wd)
                    }
                }
                Thread.sleep(executeInterval)
            }
        }
    }

    fun stop() {
        proc.destroy()
        stopThreadsFlag = true
    }

}

//fun main() {
//    val s = ScriptExecutorService()
//    val input = Scanner(System.`in`)
//    s.start()
//    while (true) {
//        try {
//            print("REPL# ")
//            val i = input.nextLine()
//            when {
//                i.startsWith("/commit") -> {
//                    val file = i.split(" ")[1]
//                    s.commitShellCommand(listOf("python", "-u", "sycm_compose.py", "-c $file"), ".")
//                    println("A")
//                }
//                i.startsWith("/stop") -> {
//                    s.stop()
//                    break
//                }
//                i.startsWith("/commands") -> {
//                    println(s.commands)
//                }
//                i.startsWith("/pwd") -> {
//                    println("Working Directory = " + System.getProperty("user.dir"))
//                }
//            }
//        } catch (e: Exception) {
//        }
//    }
//}