package me.owlsleep.owlab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns(
                        "/board/write",
                        "/board/edit/**",
                        "/board/delete/**",
                        "/board/like/**",
                        "/board/dislike/**",
                        "/board/comment",
                        "/board/comment/delete/**"
                );
    }
}
