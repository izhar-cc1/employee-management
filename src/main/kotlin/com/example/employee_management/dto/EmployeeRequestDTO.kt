// File: EmployeeRequestDTO.kt
package com.example.employee_management.dto


import com.example.employee_management.model.EmploymentType
import com.example.employee_management.model.EmployeeStatus
import com.example.employee_management.model.WorkMode
import java.time.LocalDate

data class EmployeeRequestDTO(
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val dateOfBirth: LocalDate,
    val gender: String?,
    val workEmail: String,
    val personalEmail: String?,
    val mobileNumber: String,
    val alternatePhoneNumber: String?,
    val currentAddress: String?,
    val permanentAddress: String?,
    val jobTitle: String,
    val department: String,
    val dateOfJoining: LocalDate,
    val employmentType: EmploymentType,
    val employeeStatus: EmployeeStatus = EmployeeStatus.ACTIVE,
    val workMode: WorkMode,
    val officeLocation: String?,
    val employeeCode: String,
    val systemRole: String = "EMPLOYEE", // ✅ Default value added here
    val aadhaarNumber: String,
)
