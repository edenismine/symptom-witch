package com.symptomwitch.api.symptom

import com.symptomwitch.api.common.ApiResult
import com.symptomwitch.api.common.DomainError
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer
import java.time.Instant
import java.util.UUID

data class SymptomResponse(
    val id: UUID,
    val name: String,
    val archived: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Service
class SymptomService(
    private val jdbcTemplate: JdbcTemplate,
) {
    @Transactional
    fun create(
        appUserId: UUID,
        displayName: String,
    ): ApiResult<SymptomResponse> {
        val trimmed = displayName.trim()
        validate(trimmed) ?: return ApiResult.Failure(DomainError.InvalidSymptomName("Symptom name must be between 1 and 40 characters"))
        val normalized = normalize(trimmed)
        val id = UUID.randomUUID()
        try {
            jdbcTemplate.update(
                """
                insert into symptom (id, app_user_id, display_name, normalized_name)
                values (?, ?, ?, ?)
                """.trimIndent(),
                id,
                appUserId,
                trimmed,
                normalized,
            )
        } catch (_: DuplicateKeyException) {
            return ApiResult.Failure(DomainError.DuplicateSymptomName)
        }
        val symptom = findById(appUserId, id) ?: throw IllegalStateException("Symptom not found after insert")
        return ApiResult.Success(symptom)
    }

    @Transactional
    fun rename(
        appUserId: UUID,
        symptomId: UUID,
        newName: String,
    ): ApiResult<SymptomResponse> {
        val trimmed = newName.trim()
        validate(trimmed) ?: return ApiResult.Failure(DomainError.InvalidSymptomName("Symptom name must be between 1 and 40 characters"))
        val normalized = normalize(trimmed)
        val updated =
            try {
                jdbcTemplate.update(
                    """
                    update symptom
                    set display_name = ?, normalized_name = ?, updated_at = now()
                    where id = ? and app_user_id = ?
                    """.trimIndent(),
                    trimmed,
                    normalized,
                    symptomId,
                    appUserId,
                )
            } catch (_: DuplicateKeyException) {
                return ApiResult.Failure(DomainError.DuplicateSymptomName)
            }
        if (updated == 0) return ApiResult.Failure(DomainError.SymptomNotFound(symptomId))
        val symptom = findById(appUserId, symptomId) ?: return ApiResult.Failure(DomainError.SymptomNotFound(symptomId))
        return ApiResult.Success(symptom)
    }

    fun listActive(appUserId: UUID): List<SymptomResponse> =
        jdbcTemplate.query(
            """
            select id, display_name, archived, created_at, updated_at
            from symptom
            where app_user_id = ? and archived = false
            order by lower(display_name)
            """.trimIndent(),
            symptomRowMapper,
            appUserId,
        )

    @Transactional
    fun archive(
        appUserId: UUID,
        symptomId: UUID,
    ): ApiResult<SymptomResponse> {
        val updated =
            jdbcTemplate.update(
                """
                update symptom
                set archived = true, updated_at = now()
                where id = ? and app_user_id = ?
                """.trimIndent(),
                symptomId,
                appUserId,
            )
        if (updated == 0) return ApiResult.Failure(DomainError.SymptomNotFound(symptomId))
        val symptom = findById(appUserId, symptomId) ?: return ApiResult.Failure(DomainError.SymptomNotFound(symptomId))
        return ApiResult.Success(symptom)
    }

    @Transactional
    fun reactivate(
        appUserId: UUID,
        symptomId: UUID,
    ): ApiResult<SymptomResponse> {
        val updated =
            try {
                jdbcTemplate.update(
                    """
                    update symptom
                    set archived = false, updated_at = now()
                    where id = ? and app_user_id = ?
                    """.trimIndent(),
                    symptomId,
                    appUserId,
                )
            } catch (_: DuplicateKeyException) {
                return ApiResult.Failure(DomainError.DuplicateSymptomName)
            }
        if (updated == 0) return ApiResult.Failure(DomainError.SymptomNotFound(symptomId))
        val symptom = findById(appUserId, symptomId) ?: return ApiResult.Failure(DomainError.SymptomNotFound(symptomId))
        return ApiResult.Success(symptom)
    }

    private fun findById(
        appUserId: UUID,
        symptomId: UUID,
    ): SymptomResponse? =
        jdbcTemplate
            .query(
                """
                select id, display_name, archived, created_at, updated_at
                from symptom
                where id = ? and app_user_id = ?
                """.trimIndent(),
                symptomRowMapper,
                symptomId,
                appUserId,
            ).firstOrNull()

    private fun validate(name: String): Unit? = if (name.isEmpty() || name.length > 40) null else Unit

    private fun normalize(name: String): String = Normalizer.normalize(name, Normalizer.Form.NFC).lowercase()

    private companion object {
        val symptomRowMapper =
            RowMapper<SymptomResponse> { rs, _ ->
                SymptomResponse(
                    id = UUID.fromString(rs.getString("id")),
                    name = rs.getString("display_name"),
                    archived = rs.getBoolean("archived"),
                    createdAt = rs.getTimestamp("created_at").toInstant(),
                    updatedAt = rs.getTimestamp("updated_at").toInstant(),
                )
            }
    }
}
