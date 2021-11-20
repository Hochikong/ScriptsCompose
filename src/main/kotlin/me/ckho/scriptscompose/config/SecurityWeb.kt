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
        // configuration permissions
        http.authorizeRequests()
            .antMatchers("/", "/home", "/js/**", "/css/**", "/svg/**", "/utils/hash").permitAll()
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
    fun PasswordEncoder(): PasswordEncoder{
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

}