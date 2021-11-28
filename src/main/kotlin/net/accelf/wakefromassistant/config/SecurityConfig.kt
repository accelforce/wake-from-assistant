package net.accelf.wakefromassistant.config

import net.accelf.wakefromassistant.models.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { authorizeRequests ->
            authorizeRequests.anyRequest().authenticated()
        }
            .formLogin(Customizer.withDefaults())

        return http.build()
    }

    @Bean
    fun users(configuration: Configuration) =
        User.builder()
            .username(configuration.userId)
            .password(configuration.userPassword)
            .roles("USER")
            .build()
            .let { InMemoryUserDetailsManager(it) }
}
