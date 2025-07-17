// File: AuthController.kt
package com.example.employee_management.controller


import com.example.employee_management.dto.JwtResponse
import com.example.employee_management.dto.MicrosoftSigninRequest
import com.example.employee_management.service.EmployeeService
import com.example.employee_management.util.JwtTokenUtil
import com.example.employee_management.util.MicrosoftTokenValidator
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/auth/microsoft")
class AuthController(
    private val tokenValidator: MicrosoftTokenValidator,
    private val employeeService: EmployeeService,
    private val jwtTokenUtil: JwtTokenUtil
) {

    @PostMapping("/signin")
    fun signin(@RequestHeader("Authorization") authHeader: String?): ResponseEntity<JwtResponse> {
        try {
            if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing or invalid Authorization header")
            }

            val accessToken = authHeader.removePrefix("Bearer ").trim()
            val decodedToken = tokenValidator.validateToken(accessToken)
            val email = decodedToken.getClaim("preferred_username")?.asString()
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token: email missing")

            val employee = employeeService.getActiveEmployeeByWorkEmail(email)

            val backendToken = jwtTokenUtil.generateToken(employee.workEmail, employee.systemRole)

            return ResponseEntity.ok(
                JwtResponse(
                    token = backendToken,
                    workEmail = employee.workEmail,
                    systemRole = employee.systemRole
                )
            )
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Microsoft credentials", e)
        }
    }
}
