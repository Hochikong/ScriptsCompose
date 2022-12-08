package me.ckho.scriptscompose.domain.dataclasses

data class ScriptArgSequence(
    val commandArgSeq: List<String>
){
    fun generateCommandString(): String{
        return commandArgSeq.reduce { acc, s -> "$acc $s" }.toString()
    }
}