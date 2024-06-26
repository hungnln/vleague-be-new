package com.hungnln.vleague.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
//@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {
    private static final String[] AUTH_WHITELIST = {
            "/authenticate",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/webjars/**"
    };
    @Value("${allowed.origin}")
    private String allowedOrigin;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                        .allowedOrigins(allowedOrigin,"https://vleaguev1.netlify.app")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .maxAge(4800);
    }
//    @Bean
//    public WebMvcConfigurer getCorsConfiguration() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins(allowedOrigin)
//                        .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS")
//                        .allowedHeaders("*")
//                        .maxAge(4800);
//            }
//        };
//    }

}
