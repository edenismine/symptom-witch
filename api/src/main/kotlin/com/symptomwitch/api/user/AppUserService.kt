package com.symptomwitch.api.user

import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AppUserService(
    private val appUserRepository: AppUserRepository,
) {
    @Transactional
    fun findOrCreateByAuth0Subject(auth0Subject: String): UUID {
        appUserRepository.findBySub(auth0Subject)?.let { return it.id }

        return try {
            appUserRepository.save(AppUser(id = UUID.randomUUID(), sub = auth0Subject)).id
        } catch (_: DuplicateKeyException) {
            appUserRepository.findBySub(auth0Subject)?.id
                ?: throw IllegalStateException("App user insert conflicted but no record found")
        }
    }
}
