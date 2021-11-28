package net.accelf.wakefromassistant.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.config.ClientSettings
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.RequestMatcher
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPublicKey
import java.util.*
import net.accelf.wakefromassistant.models.Configuration as AppConfiguration

@Configuration
class OAuthConfig {

    @Bean
    fun registeredClientRepository(configuration: AppConfiguration) =
        RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("google")
            .clientSecret(configuration.clientSecret)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .redirectUri("https://oauth-redirect.googleusercontent.com/r/${configuration.projectId}")
            .redirectUri("https://oauth-redirect-sandbox.googleusercontent.com/r/${configuration.projectId}")
            .scope(OidcScopes.OPENID)
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
            .build()
            .let { InMemoryRegisteredClientRepository(it) }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        val authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer<HttpSecurity>()
        val endpointsMatcher: RequestMatcher = authorizationServerConfigurer.endpointsMatcher
        http.requestMatcher(endpointsMatcher)
            .authorizeRequests { authorizeRequests ->
                authorizeRequests.anyRequest().authenticated()
            }
            .csrf { csrf: CsrfConfigurer<HttpSecurity?> ->
                csrf.ignoringRequestMatchers(endpointsMatcher)
            }
            .apply(authorizationServerConfigurer)
        return http.formLogin(Customizer.withDefaults()).build()
    }

    @Bean
    fun authorizationConsentService() =
        InMemoryOAuth2AuthorizationConsentService()

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val jwkSet = KeyPairGenerator.getInstance("RSA")
            .apply { initialize(2048) }
            .generateKeyPair()
            .let { keyPair ->
                RSAKey.Builder(keyPair.public as RSAPublicKey)
                    .privateKey(keyPair.private)
                    .keyID(UUID.randomUUID().toString())
                    .build()
            }
            .let { JWKSet(it) }
        return JWKSource<SecurityContext> { jwkSelector, _ -> jwkSelector.select(jwkSet) }
    }

    @Bean
    fun providerSettings(configuration: AppConfiguration): ProviderSettings =
        ProviderSettings.builder()
            .issuer(configuration.baseUrl)
            .build()
}
