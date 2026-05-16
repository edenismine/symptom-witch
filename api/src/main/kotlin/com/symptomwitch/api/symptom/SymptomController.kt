package com.symptomwitch.api.symptom

import com.symptomwitch.api.common.ApiResult
import com.symptomwitch.api.common.DomainError
import com.symptomwitch.api.common.DomainErrorException
import com.symptomwitch.api.user.ResolvedAppUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

data class CreateSymptomRequest(
    val name: String,
)

data class PatchSymptomRequest(
    val name: String? = null,
    val archived: Boolean? = null,
)

@RestController
@RequestMapping("/v1/symptoms")
class SymptomController(
    private val symptomService: SymptomService,
) {
    @PostMapping
    fun create(
        @ResolvedAppUser appUserId: UUID,
        @RequestBody request: CreateSymptomRequest,
    ): ResponseEntity<SymptomResponse> =
        when (val result = symptomService.create(appUserId, request.name)) {
            is ApiResult.Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.data)
            is ApiResult.Failure -> throw DomainErrorException(result.error)
        }

    @GetMapping
    fun listActive(
        @ResolvedAppUser appUserId: UUID,
    ): List<SymptomResponse> = symptomService.listActive(appUserId)

    @PatchMapping("/{id}")
    fun patch(
        @ResolvedAppUser appUserId: UUID,
        @PathVariable id: UUID,
        @RequestBody request: PatchSymptomRequest,
    ): SymptomResponse {
        if (request.name != null && request.archived != null) {
            throw DomainErrorException(DomainError.InvalidSymptomPatch)
        }
        val result =
            when {
                request.name != null -> symptomService.rename(appUserId, id, request.name)
                request.archived == true -> symptomService.archive(appUserId, id)
                request.archived == false -> symptomService.reactivate(appUserId, id)
                else -> throw DomainErrorException(DomainError.InvalidSymptomPatch)
            }
        return when (result) {
            is ApiResult.Success -> result.data
            is ApiResult.Failure -> throw DomainErrorException(result.error)
        }
    }
}
