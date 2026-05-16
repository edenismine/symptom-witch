package com.symptomwitch.api.user

import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.util.UUID

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResolvedAppUser

@Component
class AppUserArgumentResolver(
    private val appUserService: AppUserService,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(ResolvedAppUser::class.java) &&
            parameter.parameterType == UUID::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): UUID {
        val auth =
            SecurityContextHolder.getContext().authentication as? JwtAuthenticationToken
                ?: error("AppUser can only be resolved for authenticated requests")
        return appUserService.findOrCreateByAuth0Subject(auth.token.subject)
    }
}
