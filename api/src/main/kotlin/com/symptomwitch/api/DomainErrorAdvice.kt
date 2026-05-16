package com.symptomwitch.api

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

class DomainErrorException(
    val error: DomainError,
) : RuntimeException()

@RestControllerAdvice
class DomainErrorAdvice {
    @ExceptionHandler(DomainErrorException::class)
    fun handle(ex: DomainErrorException): ProblemDetail =
        when (val error = ex.error) {
            is DomainError.SymptomNotFound ->
                problem(HttpStatus.NOT_FOUND, "Symptom not found")

            is DomainError.DuplicateSymptomName ->
                problem(HttpStatus.CONFLICT, "An active symptom with that name already exists")

            is DomainError.InvalidSymptomName ->
                problem(HttpStatus.BAD_REQUEST, error.reason)

            is DomainError.InvalidSymptomPatch ->
                problem(HttpStatus.BAD_REQUEST, "Request must include name or archived, but not both")
        }

    private fun problem(
        status: HttpStatus,
        detail: String,
    ): ProblemDetail =
        ProblemDetail.forStatusAndDetail(status, detail).also {
            it.type = URI.create("about:blank")
        }
}
