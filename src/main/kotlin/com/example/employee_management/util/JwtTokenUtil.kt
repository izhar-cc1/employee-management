// File: JwtTokenUtil.kt
package com.example.employee_management.util


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenUtil {

    private val secret = "your-backend-secret-key" // Use env variable in real app
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(email: String, systemRole: String): String {
        val now = Date()
        val expiry = Date(now.time + 1000 * 60 * 60 * 2) // 2 hours

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
