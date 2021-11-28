package me.ckho.scriptscompose.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import kotlin.concurrent.thread


@Service
@Scope(value = "prototype")
class ScriptExecutorService {
    var logger: Logger = LoggerFactory.getLogger(ScriptExecutorService::class.java)
    var commands: MutableList<Array<String>> = mutableListOf()
    var workingDirs: MutableList<String> = mutableListOf()
    var stopThreads = false
    var taskRunning = false
    var stdoutAccumulate: String = ""
    var stderrAccumulate: String = ""
    lateinit var proc: Process
    var uid = "Script Executor Service -> ${UUID.randomUUID()}"

    fun commitShellCommand(command: List<String>, workingDir: String) {
        commands.add(command.toTypedArray())
        workingDirs.add(workingDir)
    }

    fun runShellCommand(command: Array<String>, workingDir: String) {
        println("Run ")
        val rt = Runtime.getRuntime()
        taskRunning = true
        proc = rt.exec(command, null, File(workingDir))
        proc.waitFor()
        taskRunning = false
    }

    fun start() {
        thread {
            while (!stopThreads) {
                if (taskRunning) {
                    try {
                        val stdInput = BufferedReader(InputStreamReader(proc.inputStream))
                        var si: String?
                        while (stdInput.readLine().also {
                                si = it
                                if (it != null) {
                                    stdoutAccumulate += "$it\n"
                                }
                            } != null) {
                            logger.info(si)
                        }
                    } catch (e: Exception) {
                    }
                }
                Thread.sleep(500)
            }
        }

        thread {
            while (!stopThreads) {
                if (taskRunning) {
                    try {
                        val stdError = BufferedReader(InputStreamReader(proc.errorStream))
                        var se: String?
                        while (stdError.readLine().also {
                                se = it
                                if (it != null) {
                                    stderrAccumulate += "$it\n"
                                }
                            } != null) {
                            logger.info(se)
                        }
                    } catch (e: Exception) {
                    }
                }
                Thread.sleep(500)
            }
        }

        thread {
            while (!stopThreads) {
                if (commands.size > 0) {
                    val c = commands[0]
                    val wd = workingDirs[0]
                    runShellCommand(c, wd)
                    commands.removeAt(0)
                    workingDirs.removeAt(0)
                }
                Thread.sleep(500)
            }
        }
    }

    fun stop() {
        proc.destroy()
        stopThreads = true
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