package com.mybusinessextractor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Configuration to load environment variables and properties files.
 * This is useful for loading values from .env files.
 */
@Configuration
@PropertySources({
    @PropertySource(value = "file:.env", ignoreResourceNotFound = true),
    @PropertySource(value = "file:backend/.env", ignoreResourceNotFound = true)
})
public class EnvConfig {
    // No implementation needed, just configuration
} 