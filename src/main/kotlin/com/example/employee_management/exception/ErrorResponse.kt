// File: ErrorResponse.kt
package com.example.employee_management.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val path: String
)