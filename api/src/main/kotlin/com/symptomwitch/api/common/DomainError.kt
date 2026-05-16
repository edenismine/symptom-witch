package com.symptomwitch.api.common

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
