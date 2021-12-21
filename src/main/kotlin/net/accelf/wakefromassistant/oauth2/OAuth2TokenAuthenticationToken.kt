package net.accelf.wakefromassistant.oauth2

import org.springframework.security.authentication.AbstractAuthenticationToken

class OAuth2TokenAuthenticationToken(
    val token: String,
) : AbstractAuthenticationToken(emptyList()) {

    var principal: String? = null
        @JvmName("_getPrincipal") get

    override fun getCredentials() = ""

    override fun getPrincipal(): Any? = principal
}
