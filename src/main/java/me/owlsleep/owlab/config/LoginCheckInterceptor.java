package me.owlsleep.owlab.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 세션에서 로그인 여부 확인
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {

            String accept = request.getHeader("Accept");
            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
                    || (accept != null && accept.contains("application/json"));

            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"로그인이 필요합니다\"}");
            } else {
                response.sendRedirect("/login?redirectURL=" + URLEncoder.encode(request.getRequestURI(), "UTF-8"));
            }

            return false;
        }

        // 로그인 되어 있으면 요청 처리 계속 진행
        return true;
    }
}