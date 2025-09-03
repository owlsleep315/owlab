package me.owlsleep.owlab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
                        "/board/comment",
                        "/board/comment/delete/**",
                        "/board/*/reaction"
                );
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
}
