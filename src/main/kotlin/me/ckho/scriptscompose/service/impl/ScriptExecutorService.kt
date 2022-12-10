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
    private val SLR: ScriptLogsRepository,
    @Autowired
    private val cache: DataCache
) {
    private var logger: Logger = LoggerFactory.getLogger(ScriptExecutorService::class.java)
    private val stdDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private var commands: MutableList<Array<String>> = mutableListOf()
    private var workingDirs: MutableList<String> = mutableListOf()
    private var stopThreadsFlag = false

    @Volatile
    private var taskRunningFlag = false
    private var stdoutAccumulate: String = "** STDOUT **\n"
    private var stderrAccumulate: String = "** STDERR **\n"
    private lateinit var proc: Process
    private var executeInterval: Long = 500L
    private lateinit var scg: ScriptGroup
    var uid = "Script Executor Service -> ${UUID.randomUUID()}"
    private var currentTaskUUID = ""
    private var currentJobType = ""
    private var currentTaskHash = ""
    private var taskInterrupt = false

    private var cursorForRepeatSequence = mutableListOf(0, (commands.size - 1))

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
        stdoutAccumulate = "<STDOUT>\n"
        stderrAccumulate = "<STDERR>\n"

        logger.info("### Run a command ###")
        val rt = Runtime.getRuntime()
        taskRunningFlag = true
        val start = getCurrentDatetimeAsDate()

        val taskHash = generateUIDForTask(
            """
            ${scg.groupName} ${scg.jobType} ${scg.interval} ${command.reduce { acc, s -> "$acc $s" }} ${scg.workingDir}
        """.trimIndent()
        )

        val logHash = when (scg.jobType) {
            "one" -> {
                generateUIDForTask(
                    """
                    ${scg.groupName} ${scg.jobType} ${scg.interval} 
                    ${command.reduce { acc, s -> acc + s }} ${scg.workingDir} one $start
                """.trimIndent()
                )
            }

            "cron" -> {
                generateUIDForTask(
                    """
                    ${scg.groupName} ${scg.jobType} ${scg.interval} 
                    ${command.reduce { acc, s -> acc + s }} ${scg.workingDir} cron ${scg.startAt} $start
                """.trimIndent()
                )
            }

            "repeat" -> {
                generateUIDForTask(
                    """
                    ${scg.groupName} ${scg.jobType} ${scg.interval} 
                    ${command.reduce { acc, s -> acc + s }} ${scg.workingDir} repeat ${scg.startAt} $start
                """.trimIndent()
                )
            }

            else -> {
                "INVALID JOB_TYPE"
            }
        }

        currentTaskUUID = logHash
        currentTaskHash = taskHash

        val r = SLR.save(
            ScriptLogsEntity(
                startTime = start,
                endTime = Date.from(string2instant("2035-01-01 00:00:00")),
                cluster = scg.cluster,
                jobGroup = scg.groupName,
                jobType = scg.jobType,
                jobInterval = scg.interval,
                jobCommand = command.reduce { acc, s -> "$acc $s" },
                jobTrigger = scg.startAt,
                workingDir = scg.workingDir,
                logHash = logHash,
                taskHash = taskHash,
                taskStatus = ScriptLogTaskStatus.RUNNING.desc,
                jobLogs = ClobProxy.generateProxy("$stdoutAccumulate \n</STDOUT>\n - \n$stderrAccumulate \n</STDERR>\n")
            )
        )

        val jobID = r.id

        proc = rt.exec(command, null, File(workingDir))
        proc.waitFor()

        // std output and error output handle
        val stdError = BufferedReader(InputStreamReader(proc.errorStream))
        val stdInput = BufferedReader(InputStreamReader(proc.inputStream))

        while (true) {
            val outputCache = stdInput.readLine()
            if (outputCache != null) {
                stdoutAccumulate += "${getCurrentDatetime()}  $outputCache\n"
            } else {
                break
            }
        }

        while (true) {
            val errorCache = stdError.readLine()
            if (errorCache != null) {
                stdoutAccumulate += "${getCurrentDatetime()}  $errorCache\n"
            } else {
                break
            }
        }

        val end = getCurrentDatetimeAsDate()

        taskRunningFlag = false
        logger.info("### Run end ###")


        // update
        val sLER = SLR.findById(jobID!!.toLong()).get()
        sLER.endTime = end
        if (taskInterrupt) {
            sLER.taskStatus = ScriptLogTaskStatus.CANCELLED.desc
        } else {
            sLER.taskStatus = ScriptLogTaskStatus.FINISHED.desc
        }
        taskInterrupt = false
        sLER.jobLogs = ClobProxy.generateProxy("$stdoutAccumulate \n</STDOUT>\n - \n$stderrAccumulate \n</STDERR>\n")

        SLR.save(sLER)
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
//        thread {
//            while (!stopThreadsFlag) {
//                if (taskRunningFlag) {
//                    try {
//                        val stdInput = BufferedReader(InputStreamReader(proc.inputStream))
//                        var si: String?
//                        while (stdInput.readLine().also {
//                                si = it
//                                if (it != null) {
//                                    stdoutAccumulate += "${getCurrentDatetime()}  $it\n"
//                                }
//                            } != null) {
//                            logger.info(si)
//                        }
//                    } catch (_: Exception) {
//                    }
//                }
//                Thread.sleep(500)
//            }
//        }

//        thread {
//            while (!stopThreadsFlag) {
//                if (taskRunningFlag) {
//                    try {
//                        val stdError = BufferedReader(InputStreamReader(proc.errorStream))
//                        var se: String?
//                        while (stdError.readLine().also {
//                                se = it
//                                if (it != null) {
//                                    stderrAccumulate += "${getCurrentDatetime()}  $it\n"
//                                }
//                            } != null) {
//                            logger.info(se)
//                        }
//                    } catch (_: Exception) {
//                    }
//                }
//                Thread.sleep(500)
//            }
//        }

        thread {
            while (!stopThreadsFlag) {
                if (commands.size > 0) {
                    if (currentJobType != JobTypes.Repeat.t) {
                        // run one by one then remove
                        val c = commands[0]
                        val wd = workingDirs[0]
//                        println(c.reduce { acc, s -> "$acc $s" })
                        runShellCommand(c, wd)
                        commands.removeAt(0)
                        workingDirs.removeAt(0)

                        if (commands.size == 0) {
                            this.stopThreadsFlag = true
                        }
                    } else {
                        // run command repeatedly
                        var c: Array<String>
                        try {
                            c = commands[this.cursorForRepeatSequence[0]]
                        }catch (error: IndexOutOfBoundsException){
                            this.cursorForRepeatSequence[0] = 0
                            c = commands[this.cursorForRepeatSequence[0]]
                        }
                        val wd = workingDirs[0]
//                        println(c.reduce { acc, s -> "$acc $s" })
                        runShellCommand(c, wd)
                        // use cursor to run command sequence repeatedly
                        if (this.cursorForRepeatSequence[0] == this.cursorForRepeatSequence[1]) {
                            this.cursorForRepeatSequence[0] = 0
                        } else {
                            this.cursorForRepeatSequence[0] += 1
                        }
                    }
                }
                Thread.sleep(executeInterval)
            }
        }

        thread {
            while (!stopThreadsFlag) {
                if (currentTaskHash in cache.needToHaltTasks) {
                    this.halt()
                    cache.needToHaltTasks.remove(currentTaskHash)
                }
                if (currentTaskHash in cache.needToInterruptTasks) {
                    this.interrupt()
                    cache.needToInterruptTasks.remove(currentTaskHash)
                }
                Thread.sleep(500)
            }
        }
    }

    fun interrupt() {
        proc.destroy()
        taskInterrupt = true
    }

    fun halt() {
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