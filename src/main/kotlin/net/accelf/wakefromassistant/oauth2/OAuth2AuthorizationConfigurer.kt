package net.accelf.wakefromassistant.oauth2

import org.springframework.beans.factory.BeanFactoryUtils
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.HttpSecurityBuilder
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter

class OAuth2AuthorizationConfigurer<B : HttpSecurityBuilder<B>> : AbstractHttpConfigurer<OAuth2AuthorizationConfigurer<B>, B>() {

    override fun configure(builder: B) {
        val authenticationManager = builder.getSharedObject(AuthenticationManager::class.java)
        val authorizationService = builder.getSharedObject(OAuth2AuthorizationService::class.java)
            ?: BeanFactoryUtils.beansOfTypeIncludingAncestors(builder.getSharedObject(ApplicationContext::class.java), OAuth2AuthorizationService::class.java).values.first()
        val authorizationFilter = OAuth2TokenAuthorizationFilter(authenticationManager)
        val authenticationProvider = OAuth2TokenAuthenticationProvider(authorizationService)
        builder.addFilterAfter(postProcess(authorizationFilter), AbstractPreAuthenticatedProcessingFilter::class.java)
        builder.authenticationProvider(postProcess(authenticationProvider))
    }
}
