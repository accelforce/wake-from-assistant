package net.accelf.wakefromassistant.config

import net.accelf.wakefromassistant.models.Configuration
import net.accelf.wakefromassistant.oauth2.OAuth2AuthorizationConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun publicSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.mvcMatcher("/login")
            .authorizeRequests { it.anyRequest().permitAll() }
            .formLogin()
        return http.build()
    }

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.mvcMatcher("/devices/**")
            .authorizeRequests { it.anyRequest().authenticated() }
            .formLogin()
        return http.build()
    }

    @Bean
    fun tokenSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.mvcMatcher("/fulfillment")
            .csrf { it.disable() }
            .anonymous { it.disable() }
            .apply(OAuth2AuthorizationConfigurer())
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
