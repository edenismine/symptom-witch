package com.symptomwitch.api

import java.util.UUID

sealed class DomainError {
    data class SymptomNotFound(
        val id: UUID,
    ) : DomainError()

    data object DuplicateSymptomName : DomainError()

    data class InvalidSymptomName(
        val reason: String,
    ) : DomainError()

    data object InvalidSymptomPatch : DomainError()
}

sealed class ApiResult<out T> {
    data class Success<T>(
        val data: T,
    ) : ApiResult<T>()

    data class Failure(
        val error: DomainError,
    ) : ApiResult<Nothing>()
}
