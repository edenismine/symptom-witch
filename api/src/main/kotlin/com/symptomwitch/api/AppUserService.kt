package com.symptomwitch.api

import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AppUserService(
    private val jdbcTemplate: JdbcTemplate,
) {
    @Transactional
    fun findOrCreateByAuth0Subject(auth0Subject: String): UUID {
        findByAuth0Subject(auth0Subject)?.let { return it }

        val id = UUID.randomUUID()
        return try {
            jdbcTemplate.update(
                "insert into app_user (id, auth0_sub) values (?, ?)",
                id,
                auth0Subject,
            )
            id
        } catch (_: DuplicateKeyException) {
            findByAuth0Subject(auth0Subject)
                ?: throw IllegalStateException("App User insert conflicted but no record was found")
        }
    }

    private fun findByAuth0Subject(auth0Subject: String): UUID? =
        jdbcTemplate
            .query(
                "select id from app_user where auth0_sub = ?",
                appUserIdMapper,
                auth0Subject,
            ).firstOrNull()

    private companion object {
        val appUserIdMapper =
            RowMapper<UUID> { resultSet, _ ->
                UUID.fromString(resultSet.getString("id"))
            }
    }
}
