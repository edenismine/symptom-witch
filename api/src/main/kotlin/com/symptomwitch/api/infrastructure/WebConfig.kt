package com.symptomwitch.api.infrastructure

import com.symptomwitch.api.user.AppUserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableJdbcAuditing
@Configuration
class WebConfig(
    private val appUserArgumentResolver: AppUserArgumentResolver,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(appUserArgumentResolver)
    }
}
