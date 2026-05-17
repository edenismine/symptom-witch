package com.symptomwitch.api.symptom

import com.symptomwitch.api.common.ApiResult
import com.symptomwitch.api.common.DomainError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer
import java.util.UUID

data class SymptomResponse(
    val id: UUID,
    val name: String,
    val archived: Boolean,
    val createdAt: java.time.Instant,
    val updatedAt: java.time.Instant,
)

private fun Symptom.toResponse() =
    SymptomResponse(
        id = id,
        name = displayName,
        archived = archived,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

@Service
class SymptomService(
    private val symptomRepository: SymptomRepository,
) {
    @Transactional
    fun create(
        appUserId: UUID,
        displayName: String,
    ): ApiResult<SymptomResponse> {
        val trimmed = displayName.trim()
        val normalized = normalize(trimmed)
        if (symptomRepository.existsByAppUserIdAndNormalizedNameAndArchivedFalse(appUserId, normalized)) {
            return ApiResult.Failure(DomainError.DuplicateSymptomName)
        }
        val symptom =
            symptomRepository.save(
                Symptom(id = UUID.randomUUID(), appUserId = appUserId, displayName = trimmed, normalizedName = normalized),
            )
        return ApiResult.Success(symptom.toResponse())
    }

    @Transactional
    fun rename(
        appUserId: UUID,
        symptomId: UUID,
        newName: String,
    ): ApiResult<SymptomResponse> {
        val trimmed = newName.trim()
        val normalized = normalize(trimmed)
        if (symptomRepository.existsByAppUserIdAndNormalizedNameAndArchivedFalse(appUserId, normalized)) {
            return ApiResult.Failure(DomainError.DuplicateSymptomName)
        }
        val symptom =
            symptomRepository.findByIdAndAppUserId(symptomId, appUserId)
                ?: return ApiResult.Failure(DomainError.SymptomNotFound(symptomId))
        val updated = symptomRepository.save(symptom.copy(displayName = trimmed, normalizedName = normalized))
        return ApiResult.Success(updated.toResponse())
    }

    fun listActive(appUserId: UUID): List<SymptomResponse> = symptomRepository.findActiveByAppUserId(appUserId).map { it.toResponse() }

    @Transactional
    fun archive(
        appUserId: UUID,
        symptomId: UUID,
    ): ApiResult<SymptomResponse> {
        val symptom =
            symptomRepository.findByIdAndAppUserId(symptomId, appUserId)
                ?: return ApiResult.Failure(DomainError.SymptomNotFound(symptomId))
        val updated = symptomRepository.save(symptom.copy(archived = true))
        return ApiResult.Success(updated.toResponse())
    }

    @Transactional
    fun reactivate(
        appUserId: UUID,
        symptomId: UUID,
    ): ApiResult<SymptomResponse> {
        val symptom =
            symptomRepository.findByIdAndAppUserId(symptomId, appUserId)
                ?: return ApiResult.Failure(DomainError.SymptomNotFound(symptomId))
        val updated = symptomRepository.save(symptom.copy(archived = false))
        return ApiResult.Success(updated.toResponse())
    }

    private fun normalize(name: String): String = Normalizer.normalize(name, Normalizer.Form.NFC).lowercase()
}
