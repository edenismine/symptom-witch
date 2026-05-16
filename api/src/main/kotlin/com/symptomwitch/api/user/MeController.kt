package com.symptomwitch.api.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/v1")
class MeController {
    @GetMapping("/me")
    fun me(
        @ResolvedAppUser appUserId: UUID,
    ): MeResponse = MeResponse(id = appUserId)
}

data class MeResponse(
    val id: UUID,
)
