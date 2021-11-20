package me.ckho.scriptscompose.service

import me.ckho.scriptscompose.domain.dataclasses.ShellReturn

interface ScriptExecutorService {
    fun runShellCommand(commands: List<String>, working_dir: String): ShellReturn
}