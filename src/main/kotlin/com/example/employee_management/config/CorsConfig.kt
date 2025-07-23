// File: WebConfig.kt
package com.example.employee_management.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // Applies to all endpoints
            .allowedOrigins("http://localhost:5173", "https://localhost:3000") // Frontend origins
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
            .allowedHeaders("*") // Allow any headers (including Authorization)
            .allowCredentials(true) // Allow cookies/authorization headers
    }
}
