package me.ckho.scriptscompose.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class LowLevelDBOps(
    @Autowired
    val jdbcTemplate: JdbcTemplate
) {
    fun recreateScriptGroupsCacheTable() {
        jdbcTemplate.execute("DROP TABLE SCRIPT_GROUPS_CACHE IF EXISTS")
        println("Drop Done")
        jdbcTemplate.execute(
            """
                create table SCRIPT_GROUPS_CACHE (
                    id bigint not null,
                    cluster varchar(255),
                    command varchar(255),
                    execute_interval integer not null,
                    group_name varchar(255),
                    job_type varchar(255),
                    start_at varchar(255),
                    working_dir varchar(255),
                    primary key (id)
                )
        """.trimIndent()
        )
        println("Create Done")
//        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE;")
//        jdbcTemplate.execute("TRUNCATE TABLE SCRIPT_GROUPS_CACHE;")
//        jdbcTemplate.execute("ALTER SEQUENCE HIBERNATE_SEQUENCE RESTART WITH 1;")
//        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE;")
    }
}