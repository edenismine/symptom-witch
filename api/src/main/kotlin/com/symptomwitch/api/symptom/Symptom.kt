package com.symptomwitch.api.symptom

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("symptom")
data class Symptom(
    @Id val id: UUID,
    val appUserId: UUID,
    val displayName: String,
    val normalizedName: String,
    val archived: Boolean = false,
    @CreatedDate val createdAt: Instant = Instant.now(),
    @LastModifiedDate val updatedAt: Instant = Instant.now(),
    @Version val version: Long = 0,
)
