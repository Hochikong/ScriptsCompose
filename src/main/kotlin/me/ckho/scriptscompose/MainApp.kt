package me.ckho.scriptscompose


import me.ckho.scriptscompose.service.ScriptExecutorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class MainApp(
    @Autowired val executor: ScriptExecutorService
) : CommandLineRunner{
    override fun run(vararg args: String?) {
        // init
        val r = executor.runShellCommand(listOf("python", "sycm_compose.py", "-c 1.txt"), "./")
        println(r)
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(MainApp::class.java, *args)
}