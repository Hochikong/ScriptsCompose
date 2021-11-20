package me.ckho.scriptscompose.service.impl

import me.ckho.scriptscompose.domain.dataclasses.ShellReturn
import me.ckho.scriptscompose.service.ScriptExecutorService
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

@Service
class ScriptExecutorServiceImpl : ScriptExecutorService {
    override fun runShellCommand(commands: List<String>, working_dir: String): ShellReturn {
        val rt = Runtime.getRuntime()
        val proc: Process = rt.exec(commands.toTypedArray())

        var stdoutAccumulate: String = ""
        var stderrAccumulate: String = ""


        thread {
            val stdInput = BufferedReader(InputStreamReader(proc.inputStream))
            var si: String?
            while (stdInput.readLine().also {
                    si = it
                    if (it != null) {
                        stdoutAccumulate += "$it\n"
                    }
                } != null) {
                println(si)
            }
        }

        thread {
            val stdError = BufferedReader(InputStreamReader(proc.errorStream))
            var se: String?
            while (stdError.readLine().also {
                    se = it
                    if (it != null) {
                        stderrAccumulate += "$it\n"
                    }
                } != null) {
                println(se)
            }
        }

        proc.waitFor()
        return ShellReturn(stdoutAccumulate, stderrAccumulate)
    }
}