package me.ckho.scriptscompose.repository

import me.ckho.scriptscompose.domain.ScriptLogsEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ScriptLogsRepository : JpaRepository<ScriptLogsEntity, Long> {
    fun findByJobGroup(group: String): List<ScriptLogsEntity?>

    fun findByJobType(type: String): List<ScriptLogsEntity?>

    fun findByJobCommandStartingWith(prefix: String): List<ScriptLogsEntity?>

    fun findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(start: Date, end: Date): List<ScriptLogsEntity?>

}