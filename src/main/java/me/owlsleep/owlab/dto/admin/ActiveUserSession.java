package me.owlsleep.owlab.dto.admin;

import java.time.LocalDateTime;

public record ActiveUserSession(
        String sessionId,
        String username,
        String role,
        LocalDateTime loggedInAt
) {
}
