package me.ckho.scriptscompose.controller

import me.ckho.scriptscompose.domain.ScriptLogsEntity
import me.ckho.scriptscompose.repository.ScriptLogsRepository
import org.hibernate.engine.jdbc.ClobProxy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat

@RestController
class HashController(
    @Autowired
    var sle: ScriptLogsRepository
) {

    val passwordEncoder: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
    private val oneTimeDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var logger: Logger = LoggerFactory.getLogger(HashController::class.java)

    @GetMapping("/utils/hash", produces = ["application/json"])
    fun cal(raw: String): String {
        val p = passwordEncoder.encode(raw)
        logger.info("A TRACE Message -> $p");
        return p
    }

    @PostMapping("/utils/insert", produces = ["application/json"])
    fun process(@RequestBody body: Map<String, Any>) {
        sle.save(
            ScriptLogsEntity(
                oneTimeDateFormat.parse(body["start_time"] as String),
                oneTimeDateFormat.parse(body["end_time"] as String),
                "group2",
                "type2",
                10,
                "python",
                "trigger1",
                "s",
                ClobProxy.generateProxy("shdsdsd7677777777777777777777777777jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj")
            )
        )
    }

    @GetMapping("/utils/query", produces = ["application/json"])
    fun query(@RequestBody body: Map<String, Any>): String {
        val r = sle.findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
            oneTimeDateFormat.parse(body["start_time"] as String),
            oneTimeDateFormat.parse(body["end_time"] as String)
        )

        if (r.isNotEmpty()){
            val tmp = mapOf("data" to r.map { it.toString() })
            return tmp.toString()
        }

        return ""
    }
}