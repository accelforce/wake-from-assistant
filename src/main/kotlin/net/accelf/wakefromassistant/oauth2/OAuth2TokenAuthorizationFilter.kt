package net.accelf.wakefromassistant.oauth2

import org.springframework.http.HttpStatus
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class OAuth2TokenAuthorizationFilter(
    private val authenticationManager: AuthenticationManager,
): OncePerRequestFilter() {

    private val authenticationConverter = OAuth2TokenAuthenticationConverter()
    private val errorMessageConverter = OAuth2ErrorHttpMessageConverter()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        SecurityContextHolder.getContext().authentication =
            (authenticationConverter.convert(request)
                ?.runCatching { authenticationManager.authenticate(this) }
                ?: Result.failure<Authentication>(OAuth2AuthenticationException(OAuth2ErrorCodes.UNSUPPORTED_TOKEN_TYPE)))
                .onFailure { onAuthenticationFailure(response, it as OAuth2AuthenticationException) }
                .getOrNull() ?: return
        filterChain.doFilter(request, response)
    }

    private fun onAuthenticationFailure(response: HttpServletResponse, exception: OAuth2AuthenticationException) {
        SecurityContextHolder.clearContext()
        val httpResponse = ServletServerHttpResponse(response)
        httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED)
        errorMessageConverter.write(exception.error, null, httpResponse)
    }
}
