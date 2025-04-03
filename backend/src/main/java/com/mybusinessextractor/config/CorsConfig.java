package com.mybusinessextractor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for CORS settings.
 */
@Configuration
public class CorsConfig {

    @Value("${spring.webmvc.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${spring.webmvc.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${spring.webmvc.cors.allowed-headers}")
    private String allowedHeaders;

    /**
     * Creates a CORS filter bean with custom configuration.
     *
     * @return The configured CORS filter
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        corsConfiguration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        corsConfiguration.setAllowedHeaders(List.of(allowedHeaders));
        corsConfiguration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsFilter(source);
    }
} 