package me.owlsleep.owlab.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import me.owlsleep.owlab.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "관리자 권한이 필요합니다.");
            return false;
        }

        Object attribute = session.getAttribute("loginUser");
        if (!(attribute instanceof User user) || !user.isAdmin()) {
            String accept = request.getHeader("Accept");
            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
                    || (accept != null && accept.contains("application/json"));

            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\":\"관리자 권한이 필요합니다\"}");
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 필요합니다.");
            }
            return false;
        }

        return true;
    }
}
