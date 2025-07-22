// File: JwtTokenUtil.kt
package com.example.employee_management.util


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenUtil {

    // Read JWT_SECRET from environment variable, fallback to default if missing
    private val secret: String = System.getenv("JWT_SECRET") ?: "your-backend-secret-key"
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(email: String, systemRole: String): String {
        val now = Date()
        val expiry = Date(now.time + 1000 * 60 * 60) // 1 hour

        return JWT.create()
            .withIssuer("employee-app")
            .withSubject(email)
            .withClaim("role", systemRole)
            .withIssuedAt(now)
            .withExpiresAt(expiry)
            .sign(algorithm)
    }

    fun validateToken(token: String): DecodedJWT {
        return JWT.require(algorithm)
            .withIssuer("employee-app")
            .build()
            .verify(token)
    }

    fun extractEmail(token: String): String = validateToken(token).subject
    fun extractRole(token: String): String = validateToken(token).getClaim("role").asString()
}
