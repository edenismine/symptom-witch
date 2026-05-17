package com.symptomwitch.api.user

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface AppUserRepository : CrudRepository<AppUser, UUID> {
    fun findBySub(sub: String): AppUser?
}
