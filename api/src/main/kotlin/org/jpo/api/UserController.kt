package org.jpo.api

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @GetMapping("/api/user/me")
    fun getMe(@AuthenticationPrincipal principal: OAuth2User?): Map<String, Any?> {
        if (principal == null) {
            return emptyMap()
        }
        return mapOf(
            "name" to principal.getAttribute<String>("name"),
            "email" to principal.getAttribute<String>("email"),
            "picture" to principal.getAttribute<String>("picture")
        )
    }
}
