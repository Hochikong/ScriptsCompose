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
        jdbcTemplate.execute("TRUNCATE TABLE script_groups_cache;")
        println("Truncate table script_groups_cache done.")
//        jdbcTemplate.execute("DROP TABLE script_groups_cache")
//        println("Drop Done")
//        jdbcTemplate.execute(
//            """
//                create table script_groups_cache (
//                    id bigint not null,
//                    cluster varchar(255),
//                    command varchar(255),
//                    execute_interval integer not null,
//                    group_name varchar(255),
//                    job_type varchar(255),
//                    start_at varchar(255),
//                    working_dir varchar(255),
//                    primary key (id)
//                )
//        """.trimIndent()
//        )
//        println("Create Done")
    }
}