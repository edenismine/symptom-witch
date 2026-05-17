package com.symptomwitch.api.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("cors")
data class CorsProperties(
    val allowedOrigins: List<String> = listOf("http://localhost:5173"),
    val allowedMethods: List<String> = listOf("GET", "POST", "PATCH", "DELETE", "OPTIONS"),
    val allowedHeaders: List<String> = listOf("*"),
    val allowCredentials: Boolean = false,
    val maxAge: Long = 3600,
)
