package net.accelf.wakefromassistant.oauth2

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationConverter
import javax.servlet.http.HttpServletRequest

class OAuth2TokenAuthenticationConverter: AuthenticationConverter {
    override fun convert(request: HttpServletRequest?): Authentication? =
        request?.getHeader(HEADER)
            ?.takeIf { it.startsWith(PREFIX) }
            ?.drop(PREFIX.length)
            ?.let { token -> OAuth2TokenAuthenticationToken(token) }

    companion object {
        private const val HEADER = "Authorization"
        private const val PREFIX = "Bearer "
    }
}
