package me.ckho.scriptscompose


import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class MainApp : CommandLineRunner{
    override fun run(vararg args: String?) {
        // init

    }

}

fun main(args: Array<String>) {
    SpringApplication.run(MainApp::class.java, *args)
}