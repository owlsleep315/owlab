package me.owlsleep.owlab.config;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import me.owlsleep.owlab.dto.admin.ActiveUserSession;
import me.owlsleep.owlab.entity.User;
public class SessionTracker implements HttpSessionAttributeListener, HttpSessionListener {

    private final Map<String, ActiveUserSession> activeUsers = new ConcurrentHashMap<>();

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (isLoginUserAttribute(event)) {
            trackSession(event.getSession(), (User) event.getValue());
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (isLoginUserAttribute(event)) {
            activeUsers.remove(event.getSession().getId());
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if (isLoginUserAttribute(event)) {
            HttpSession session = event.getSession();
            Object value = session.getAttribute("loginUser");
            if (value instanceof User user) {
                trackSession(session, user);
            }
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activeUsers.remove(se.getSession().getId());
    }

    public List<ActiveUserSession> getActiveUsers() {
        return activeUsers.values().stream()
                .sorted(Comparator.comparing(ActiveUserSession::loggedInAt).reversed())
                .collect(Collectors.toList());
    }

    public int getActiveUserCount() {
        return activeUsers.size();
    }

    private boolean isLoginUserAttribute(HttpSessionBindingEvent event) {
        return "loginUser".equals(event.getName()) && event.getValue() instanceof User;
    }

    private void trackSession(HttpSession session, User user) {
        if (session == null || user == null) {
            return;
        }
        activeUsers.put(session.getId(), new ActiveUserSession(
                session.getId(),
                user.getUsername(),
                user.getRole(),
                LocalDateTime.now()
        ));
    }
}
