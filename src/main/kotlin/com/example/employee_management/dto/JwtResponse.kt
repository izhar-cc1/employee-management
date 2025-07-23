// File: JwtResponse.kt
package com.example.employee_management.dto

data class JwtResponse(
    val token: String,
    val name: String,
    val workEmail: String,
    val systemRole: String
)