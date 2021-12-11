package me.ckho.scriptscompose.domain.enums

enum class JobTypes(val t: String) {
    OneTime("one"),
    Cron("cron"),
    Repeat("repeat")
}