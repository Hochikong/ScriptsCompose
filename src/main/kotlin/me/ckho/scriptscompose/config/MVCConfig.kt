package me.ckho.scriptscompose.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class MvcConfig : WebMvcConfigurer {
    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/home").setViewName("index")
        registry.addViewController("/").setViewName("index")
        registry.addViewController("/login").setViewName("login")
        registry.addViewController("/cronDash").setViewName("cronDash")
        registry.addViewController("/oneDash").setViewName("oneDash")
//        registry.addViewController("/allTasks").setViewName("allTasks")
//        registry.addViewController("/allGroups").setViewName("allGroups")
        registry.addViewController("/taskDetails").setViewName("taskDetails")
        registry.addViewController("/allRunning").setViewName("allRunning")
    }
}