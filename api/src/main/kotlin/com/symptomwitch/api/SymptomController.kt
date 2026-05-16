package com.symptomwitch.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

data class CreateSymptomRequest(
    val name: String,
)

data class PatchSymptomRequest(
    val name: String? = null,
    val archived: Boolean? = null,
)

data class ErrorResponse(
    val message: String,
)

@RestController
@RequestMapping("/v1/symptoms")
class SymptomController(
    private val appUserService: AppUserService,
    private val symptomService: SymptomService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        authentication: JwtAuthenticationToken,
        @RequestBody request: CreateSymptomRequest,
    ): SymptomResponse {
        val appUserId = appUserService.findOrCreateByAuth0Subject(authentication.token.subject)
        return symptomService.create(appUserId, request.name)
    }

    @GetMapping
    fun listActive(authentication: JwtAuthenticationToken): List<SymptomResponse> {
        val appUserId = appUserService.findOrCreateByAuth0Subject(authentication.token.subject)
        return symptomService.listActive(appUserId)
    }

    @PatchMapping("/{id}")
    fun patch(
        authentication: JwtAuthenticationToken,
        @PathVariable id: UUID,
        @RequestBody request: PatchSymptomRequest,
    ): ResponseEntity<Any> {
        if (request.name != null && request.archived != null) {
            return ResponseEntity.badRequest().body(ErrorResponse("Cannot rename and change archived status in the same request"))
        }
        val appUserId = appUserService.findOrCreateByAuth0Subject(authentication.token.subject)
        val result =
            when {
                request.name != null -> symptomService.rename(appUserId, id, request.name)
                request.archived == true -> symptomService.archive(appUserId, id)
                request.archived == false -> symptomService.reactivate(appUserId, id)
                else -> return ResponseEntity.badRequest().body(ErrorResponse("Request must include name or archived"))
            }
        return ResponseEntity.ok(result)
    }

    @ExceptionHandler(SymptomNotFoundException::class)
    fun handleNotFound(): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse("Symptom not found"))

    @ExceptionHandler(DuplicateSymptomNameException::class)
    fun handleDuplicate(): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse("An active symptom with that name already exists"))

    @ExceptionHandler(InvalidSymptomNameException::class)
    fun handleInvalidName(ex: InvalidSymptomNameException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(ErrorResponse(ex.message ?: "Invalid symptom name"))
}
