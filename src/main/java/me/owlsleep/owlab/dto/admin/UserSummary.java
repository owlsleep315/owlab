package me.owlsleep.owlab.dto.admin;

import java.time.LocalDateTime;

public record UserSummary(
        Long id,
        String username,
        String role,
        LocalDateTime lastLoginAt,
        long loginCount
) {
}
