package com.symptomwitch.api

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/v1")
class MeController(
    private val appUserService: AppUserService,
) {
    @GetMapping("/me")
    fun me(authentication: JwtAuthenticationToken): MeResponse {
        val appUserId = appUserService.findOrCreateByAuth0Subject(authentication.token.subject)
        return MeResponse(id = appUserId)
    }
}

data class MeResponse(
    val id: UUID,
)
