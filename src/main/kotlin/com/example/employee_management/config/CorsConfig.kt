// File: CorsConfig.kt
package com.example.employee_management.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            // Allow specific origins (replace with your frontend URL)
            allowedOrigins = listOf(
                "http://localhost:3000",
                "https://localhost:3000",
                "http://localhost:3001",
                "https://your-frontend-domain.com"
            )

            // Allow specific methods
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")

            // Allow specific headers
            allowedHeaders = listOf(
                "Authorization",
                "Content-Type",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
            )

            // Allow credentials
            allowCredentials = true

            // Expose headers
            exposedHeaders = listOf("Authorization")
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}