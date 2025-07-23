// File: EmployeeMapper.kt
package com.example.employee_management.mapper


import com.example.employee_management.dto.EmployeeRequestDTO
import com.example.employee_management.dto.EmployeeResponseDTO
import com.example.employee_management.model.Employee
import org.springframework.stereotype.Component
import java.util.*

@Component
class EmployeeMapper {

    fun toEntity(dto: EmployeeRequestDTO): Employee {
        return Employee(
            firstName = dto.firstName,
            lastName = dto.lastName,
            middleName = dto.middleName,
            dateOfBirth = dto.dateOfBirth,
            gender = dto.gender,
            workEmail = dto.workEmail,
            personalEmail = dto.personalEmail,
            mobileNumber = dto.mobileNumber,
            alternatePhoneNumber = dto.alternatePhoneNumber,
            currentAddress = dto.currentAddress,
            permanentAddress = dto.permanentAddress,
            jobTitle = dto.jobTitle,
            department = dto.department,
            dateOfJoining = dto.dateOfJoining,
            employmentType = dto.employmentType,
            employeeStatus = dto.employeeStatus,
            workMode = dto.workMode,
            officeLocation = dto.officeLocation,
            employeeCode = dto.employeeCode,
            systemRole = dto.systemRole,
            aadhaarNumber = dto.aadhaarNumber
        )
    }

    fun toResponseDto(employee: Employee): EmployeeResponseDTO {
        return EmployeeResponseDTO(
            id = employee.id,
            employeeId = employee.employeeId,
            firstName = employee.firstName,
            lastName = employee.lastName,
            dateOfBirth = employee.dateOfBirth,
            workEmail = employee.workEmail,
            mobileNumber = employee.mobileNumber,
            jobTitle = employee.jobTitle,
            department = employee.department,
            dateOfJoining = employee.dateOfJoining,
            employmentType = employee.employmentType,
            employeeStatus = employee.employeeStatus,
            workMode = employee.workMode,
            employeeCode = employee.employeeCode,
            systemRole = employee.systemRole,
            aadhaarNumber = employee.aadhaarNumber
        )
    }
}
