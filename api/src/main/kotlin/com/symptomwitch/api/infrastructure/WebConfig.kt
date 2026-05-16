package com.symptomwitch.api.infrastructure

import com.symptomwitch.api.user.AppUserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val appUserArgumentResolver: AppUserArgumentResolver,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(appUserArgumentResolver)
    }
}
