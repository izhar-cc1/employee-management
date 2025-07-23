// File: EmployeeResponseDTO.kt
package com.example.employee_management.dto

import com.example.employee_management.model.EmploymentType
import com.example.employee_management.model.EmployeeStatus
import com.example.employee_management.model.WorkMode
import java.time.LocalDate
import java.util.*

data class EmployeeResponseDTO(
    val id: Long?,
    val employeeId: UUID,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: LocalDate,
    val workEmail: String,
    val mobileNumber: String,
    val jobTitle: String,
    val department: String,
    val dateOfJoining: LocalDate,
    val employmentType: EmploymentType,
    val employeeStatus: EmployeeStatus,
    val workMode: WorkMode,
    val employeeCode: String,
    val systemRole: String,
    val aadhaarNumber: String,
)
