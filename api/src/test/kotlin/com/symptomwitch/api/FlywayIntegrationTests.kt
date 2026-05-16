package com.symptomwitch.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.assertEquals

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class FlywayIntegrationTests {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun flywayRunsInitialMigration() {
        val count =
            jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_name = 'app_user'",
                Int::class.java,
            )

        assertEquals(1, count)
    }

    @Test
    fun flywayRunsSymptomMigration() {
        val count =
            jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_name = 'symptom'",
                Int::class.java,
            )

        assertEquals(1, count)
    }
}
