package com.symptomwitch.api.user

import com.jayway.jsonpath.JsonPath
import com.symptomwitch.api.TestcontainersConfiguration
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@Import(MeControllerTestJwtDecoderConfig::class, TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@SpringBootTest
class MeControllerIntegrationTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun meEndpointReturnsStableAppUserIdentity() {
        val firstResponse =
            mockMvc
                .perform(get("/v1/me").header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").isString)
                .andExpect(content().string(not(containsString("auth0|user-123"))))
                .andReturn()
                .response
                .contentAsString

        val firstId = JsonPath.parse(firstResponse).read<String>("$.id")

        mockMvc
            .perform(get("/v1/me").header("Authorization", "Bearer valid-token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(firstId))
            .andExpect(content().string(not(containsString("auth0|user-123"))))
    }

    @Test
    fun meEndpointRejectsInvalidBearerToken() {
        mockMvc
            .perform(get("/v1/me").header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun meEndpointRejectsExpiredBearerToken() {
        mockMvc
            .perform(get("/v1/me").header("Authorization", "Bearer expired-token"))
            .andExpect(status().isUnauthorized)
    }
}

@TestConfiguration
class MeControllerTestJwtDecoderConfig {
    @Bean
    @Primary
    fun testJwtDecoder(): JwtDecoder =
        JwtDecoder { token ->
            when (token) {
                "valid-token" ->
                    Jwt
                        .withTokenValue(token)
                        .header("alg", "RS256")
                        .subject("auth0|user-123")
                        .issuer("https://symptom-witch.test/")
                        .audience(listOf("https://api.symptomwitch.local"))
                        .issuedAt(Instant.now().minusSeconds(60))
                        .expiresAt(Instant.now().plusSeconds(300))
                        .build()

                "expired-token" -> throw BadJwtException("Jwt expired at ${Instant.now().minusSeconds(1)}")
                else -> throw BadJwtException("Invalid token")
            }
        }
}
