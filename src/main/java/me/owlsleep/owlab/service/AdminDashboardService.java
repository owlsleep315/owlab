package me.owlsleep.owlab.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.owlsleep.owlab.config.SessionTracker;
import me.owlsleep.owlab.dto.admin.ActiveUserSession;
import me.owlsleep.owlab.dto.admin.AdminDashboardData;
import me.owlsleep.owlab.dto.admin.UserSummary;
import me.owlsleep.owlab.entity.User;
import me.owlsleep.owlab.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final SessionTracker sessionTracker;

    public AdminDashboardService(UserRepository userRepository, SessionTracker sessionTracker) {
        this.userRepository = userRepository;
        this.sessionTracker = sessionTracker;
    }

    public AdminDashboardData getDashboardData() {
        long totalUsers = userRepository.count();
        long adminUsers = userRepository.countByRoleIgnoreCase("ADMIN");
        long regularUsers = Math.max(0, totalUsers - adminUsers);
        long totalLoginCount = Optional.ofNullable(userRepository.sumLoginCount()).orElse(0L);
        double averageLoginPerUser = totalUsers == 0 ? 0.0 : (double) totalLoginCount / totalUsers;

        List<ActiveUserSession> activeUsers = sessionTracker.getActiveUsers().stream()
                .limit(20)
                .collect(Collectors.toList());
        int activeUserCount = activeUsers.size();

        List<UserSummary> recentLogins = userRepository.findTop10ByOrderByLastLoginAtDesc().stream()
                .map(this::toUserSummary)
                .collect(Collectors.toList());

        return new AdminDashboardData(
                totalUsers,
                adminUsers,
                regularUsers,
                totalLoginCount,
                averageLoginPerUser,
                activeUserCount,
                activeUsers,
                recentLogins
        );
    }

    private UserSummary toUserSummary(User user) {
        long loginCount = user.getLoginCount();
        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getLastLoginAt(),
                loginCount
        );
    }
}
