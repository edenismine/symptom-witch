package com.symptomwitch.api.symptom

import com.symptomwitch.api.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@Import(SymptomControllerTestJwtDecoderConfig::class, TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@SpringBootTest
class SymptomControllerIntegrationTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun getActiveSymptomsSortedAlphabetically() {
        // isolated user so other tests don't pollute the list
        mockMvc
            .perform(
                post("/v1/symptoms")
                    .header("Authorization", "Bearer sort-test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Nausea"}"""),
            ).andExpect(status().isCreated)
        mockMvc
            .perform(
                post("/v1/symptoms")
                    .header("Authorization", "Bearer sort-test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Anxiety"}"""),
            ).andExpect(status().isCreated)
        mockMvc
            .perform(
                post("/v1/symptoms")
                    .header("Authorization", "Bearer sort-test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Migraine"}"""),
            ).andExpect(status().isCreated)

        mockMvc
            .perform(get("/v1/symptoms").header("Authorization", "Bearer sort-test-token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("Anxiety"))
            .andExpect(jsonPath("$[1].name").value("Migraine"))
            .andExpect(jsonPath("$[2].name").value("Nausea"))
    }

    @Test
    fun postSymptomRejectsNameLongerThan40Chars() {
        val longName = "A".repeat(41)
        mockMvc
            .perform(
                post("/v1/symptoms")
                    .header("Authorization", "Bearer valid-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"$longName"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.detail").isString)
    }

    @Test
    fun postSymptomRejectsDuplicateActiveName() {
        mockMvc
            .perform(
                post("/v1/symptoms")
                    .header("Authorization", "Bearer valid-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Fatigue"}"""),
            ).andExpect(status().isCreated)

        mockMvc
            .perform(
                post("/v1/symptoms")
                    .header("Authorization", "Bearer valid-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"fatigue"}"""),
            ).andExpect(status().isConflict)
            .andExpect(jsonPath("$.detail").isString)
    }

    @Test
    fun patchRenamesSymptom() {
        val created =
            mockMvc
                .perform(
                    post("/v1/symptoms")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"name":"Dizzy"}"""),
                ).andExpect(status().isCreated)
                .andReturn()
                .response
                .contentAsString
        val id =
            com.jayway.jsonpath.JsonPath
                .parse(created)
                .read<String>("$.id")

        mockMvc
            .perform(
                patch("/v1/symptoms/$id")
                    .header("Authorization", "Bearer valid-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Dizziness"}"""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Dizziness"))
            .andExpect(jsonPath("$.id").value(id))
    }

    @Test
    fun patchArchivesSymptomAndItDisappearsFromActiveList() {
        val created =
            mockMvc
                .perform(
                    post("/v1/symptoms")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"name":"Brain fog"}"""),
                ).andReturn()
                .response
                .contentAsString
        val id =
            com.jayway.jsonpath.JsonPath
                .parse(created)
                .read<String>("$.id")

        mockMvc
            .perform(
                patch("/v1/symptoms/$id")
                    .header("Authorization", "Bearer valid-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"archived":true}"""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.archived").value(true))

        mockMvc
            .perform(get("/v1/symptoms").header("Authorization", "Bearer valid-token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[?(@.id == '$id')]").isEmpty)
    }

    @Test
    fun patchReactivatesArchivedSymptomWithSameId() {
        val created =
            mockMvc
                .perform(
                    post("/v1/symptoms")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"name":"Joint pain"}"""),
                ).andReturn()
                .response
                .contentAsString
        val id =
            com.jayway.jsonpath.JsonPath
                .parse(created)
                .read<String>("$.id")

        mockMvc.perform(
            patch("/v1/symptoms/$id")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"archived":true}"""),
        )

        mockMvc
            .perform(
                patch("/v1/symptoms/$id")
                    .header("Authorization", "Bearer valid-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"archived":false}"""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.archived").value(false))
            .andExpect(jsonPath("$.id").value(id))

        mockMvc
            .perform(get("/v1/symptoms").header("Authorization", "Bearer valid-token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[?(@.id == '$id')]").isNotEmpty)
    }

    @Test
    fun patchReturns404WhenSymptomBelongsToAnotherUser() {
        val created =
            mockMvc
                .perform(
                    post("/v1/symptoms")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"name":"Insomnia"}"""),
                ).andReturn()
                .response
                .contentAsString
        val id =
            com.jayway.jsonpath.JsonPath
                .parse(created)
                .read<String>("$.id")

        mockMvc
            .perform(
                patch("/v1/symptoms/$id")
                    .header("Authorization", "Bearer valid-token-user-2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"archived":true}"""),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun getDoesNotReturnOtherUsersSymptoms() {
        mockMvc
            .perform(
                post("/v1/symptoms")
                    .header("Authorization", "Bearer valid-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Cramps"}"""),
            ).andExpect(status().isCreated)

        mockMvc
            .perform(get("/v1/symptoms").header("Authorization", "Bearer valid-token-user-2"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[?(@.name == 'Cramps')]").isEmpty)
    }

    @Test
    fun postSymptomCreatesSymptomAndReturns201() {
        mockMvc
            .perform(
                post("/v1/symptoms")
                    .header("Authorization", "Bearer valid-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Headache"}"""),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isString)
            .andExpect(jsonPath("$.name").value("Headache"))
            .andExpect(jsonPath("$.archived").value(false))
            .andExpect(jsonPath("$.createdAt").isString)
            .andExpect(jsonPath("$.updatedAt").isString)
    }

    @Test
    fun symptomNotFoundReturnsApplicationProblemJson() {
        val nonExistentId = java.util.UUID.randomUUID()
        mockMvc
            .perform(
                patch("/v1/symptoms/$nonExistentId")
                    .header("Authorization", "Bearer valid-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"archived":true}"""),
            ).andExpect(status().isNotFound)
            .andExpect(
                content().contentTypeCompatibleWith("application/problem+json"),
            )
    }
}

@TestConfiguration
class SymptomControllerTestJwtDecoderConfig {
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

                "valid-token-user-2" ->
                    Jwt
                        .withTokenValue(token)
                        .header("alg", "RS256")
                        .subject("auth0|user-456")
                        .issuer("https://symptom-witch.test/")
                        .audience(listOf("https://api.symptomwitch.local"))
                        .issuedAt(Instant.now().minusSeconds(60))
                        .expiresAt(Instant.now().plusSeconds(300))
                        .build()

                "sort-test-token" ->
                    Jwt
                        .withTokenValue(token)
                        .header("alg", "RS256")
                        .subject("auth0|user-sort")
                        .issuer("https://symptom-witch.test/")
                        .audience(listOf("https://api.symptomwitch.local"))
                        .issuedAt(Instant.now().minusSeconds(60))
                        .expiresAt(Instant.now().plusSeconds(300))
                        .build()

                else -> throw BadJwtException("Invalid token")
            }
        }
}
