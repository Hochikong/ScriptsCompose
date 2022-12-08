package me.ckho.scriptscompose.repository

import me.ckho.scriptscompose.domain.ScriptGroupsCacheEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ScriptGroupsCacheRepository : JpaRepository<ScriptGroupsCacheEntity, Long> {
    fun findByJobType(type: String): List<ScriptGroupsCacheEntity?>
}