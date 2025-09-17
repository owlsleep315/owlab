package me.owlsleep.owlab.dto.admin;

import java.util.List;

public record AdminDashboardData(
        long totalUsers,
        long adminUsers,
        long regularUsers,
        long totalLoginCount,
        double averageLoginPerUser,
        int activeUserCount,
        List<ActiveUserSession> activeUsers,
        List<UserSummary> recentLogins
) {
}
