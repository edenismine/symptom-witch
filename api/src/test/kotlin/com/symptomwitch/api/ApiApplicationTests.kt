package com.symptomwitch.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Import(TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@SpringBootTest
class ApiApplicationTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun healthEndpointReturnsOk() {
        mockMvc
            .perform(get("/health"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"status":"ok"}"""))
    }

    @Test
    fun meEndpointRejectsMissingBearerToken() {
        mockMvc
            .perform(get("/v1/me"))
            .andExpect(status().isUnauthorized)
    }
}
