package com.symptomwitch.api.symptom

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface SymptomRepository : CrudRepository<Symptom, UUID> {
    @Query("select * from symptom where app_user_id = :appUserId and archived = false order by lower(display_name)")
    fun findActiveByAppUserId(appUserId: UUID): List<Symptom>

    fun findByIdAndAppUserId(
        id: UUID,
        appUserId: UUID,
    ): Symptom?

    fun existsByAppUserIdAndNormalizedNameAndArchivedFalse(
        appUserId: UUID,
        normalizedName: String,
    ): Boolean
}
