package com.symptomwitch.api

import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JwtAudienceValidatorTests {
    private val validator = JwtAudienceValidator("https://api.symptomwitch.local")

    @Test
    fun acceptsJwtWithConfiguredAudience() {
        val result = validator.validate(jwtWithAudience("https://api.symptomwitch.local"))

        assertFalse(result.hasErrors())
    }

    @Test
    fun rejectsJwtWithoutConfiguredAudience() {
        val result = validator.validate(jwtWithAudience("api://other"))

        assertTrue(result.hasErrors())
    }

    private fun jwtWithAudience(audience: String): Jwt =
        Jwt
            .withTokenValue("token")
            .header("alg", "RS256")
            .subject("auth0|user-123")
            .issuer("https://symptom-witch.test/")
            .audience(listOf(audience))
            .issuedAt(Instant.now().minusSeconds(60))
            .expiresAt(Instant.now().plusSeconds(300))
            .build()
}
