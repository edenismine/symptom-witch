package com.symptomwitch.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(
    properties = [
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration",
    ],
)
class ApiApplicationTests {
    @Autowired
    lateinit var context: WebApplicationContext

    @Test
    fun healthEndpointReturnsOk() {
        val mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        mockMvc
            .perform(get("/health"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"status":"ok"}"""))
    }
}
