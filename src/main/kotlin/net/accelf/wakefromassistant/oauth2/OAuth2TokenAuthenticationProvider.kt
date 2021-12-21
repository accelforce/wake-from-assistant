package net.accelf.wakefromassistant.oauth2

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import kotlin.reflect.full.isSubclassOf

class OAuth2TokenAuthenticationProvider(
    private val authorizationService: OAuth2AuthorizationService,
): AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val tokenAuthentication = authentication as OAuth2TokenAuthenticationToken

        authorizationService.findByToken(
            tokenAuthentication.token,
            OAuth2TokenType.ACCESS_TOKEN,
        )?.let {
            tokenAuthentication.apply {
                principal = it.principalName
                isAuthenticated = true
            }
        } ?: throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_TOKEN)

        return tokenAuthentication
    }

    override fun supports(authentication: Class<*>?) =
        authentication?.kotlin?.isSubclassOf(OAuth2TokenAuthenticationToken::class) ?: false
}
