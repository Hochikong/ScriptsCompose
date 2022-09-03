package me.ckho.scriptscompose.config

import me.ckho.scriptscompose.service.impl.UserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    @Autowired
    val userService: UserDetailsService
) : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.formLogin().defaultSuccessUrl("/", true)
        http.headers().frameOptions().disable()
        // configuration permissions
        // only disable csrf than any post requests can be received by rest controller
        http.csrf().disable()
        http.authorizeRequests()
            .antMatchers(
                "/js/**",
                "/css/**",
                "/svg/**",
                "/utils/**",
//                "/groups/allGroups/**",
//                "/groups/allGroups/byType",
//                "/tasks/allTasks",
//                "/tasks/allTasks/byType",
//                "/tasks/allRunning",
//                "/tasks/interrupt",
//                "/tasks/detail",
//                "/logs/brief",
//                "/logs/detail"
            ).permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .permitAll()
            .and()
            .logout()
            .permitAll()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        // auth manager
//        val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
//        auth.inMemoryAuthentication().withUser("user")
//            .password(passwordEncoder.encode("password"))
//            .authorities("ROLE_USER")
        auth.userDetailsService(userService)
    }

    @Bean
    fun PasswordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

}