package me.owlsleep.owlab.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;
    private final AdminCheckInterceptor adminCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns(
                        "/board/write",
                        "/board/edit/**",
                        "/board/delete/**",
                        "/board/comment",
                        "/board/comment/delete/**",
                        "/board/*/reaction",
                        "/admin/**"
                );

        registry.addInterceptor(adminCheckInterceptor)
                .addPathPatterns("/admin/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://api.owlsleep.me",
                        "https://couponmoa.netlify.app",
                        "http://localhost:3000",
                        "http://43.200.180.72:8080"
                )
                .allowedMethods("GET","POST","PATCH","PUT","DELETE","OPTIONS");
    }

    @Bean
    public SessionTracker sessionTracker() {
        return new SessionTracker();
    }

    @Bean
    public ServletListenerRegistrationBean<SessionTracker> sessionTrackerListener(SessionTracker sessionTracker) {
        return new ServletListenerRegistrationBean<>(sessionTracker);
    }
}
