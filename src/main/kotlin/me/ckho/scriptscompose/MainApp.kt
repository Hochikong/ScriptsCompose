package me.ckho.scriptscompose

import me.ckho.scriptscompose.service.impl.QuartzService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class MainApp(
    @Autowired val qz: QuartzService
) : CommandLineRunner{
    override fun run(vararg args: String?) {
        // init
        println("Add quartz job ...")
        qz.addJob("2021-11-20 21:02:00")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(MainApp::class.java, *args)
}