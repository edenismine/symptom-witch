package com.symptomwitch.api.common

sealed class ApiResult<out T> {
    data class Success<T>(
        val data: T,
    ) : ApiResult<T>()

    data class Failure(
        val error: DomainError,
    ) : ApiResult<Nothing>()
}
