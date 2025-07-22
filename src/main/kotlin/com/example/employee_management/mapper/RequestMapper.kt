// File: RequestMapper.kt
package com.example.employee_management.mapper

import com.example.employee_management.dto.CreateRequestDTO
import com.example.employee_management.dto.RequestResponseDTO
import com.example.employee_management.model.EmployeeRequest
import com.example.employee_management.model.Employee
import org.springframework.stereotype.Component

@Component
class RequestMapper {

    fun toEntity(dto: CreateRequestDTO, employee: Employee): EmployeeRequest {
        return EmployeeRequest(
            employee = employee,
            requestType = dto.requestType,
            title = dto.title,
            description = dto.description,
            startDate = dto.startDate,
            endDate = dto.endDate,
            numberOfDays = dto.numberOfDays,
            amount = dto.amount,
            currency = dto.currency,
            justification = dto.justification,
            priority = dto.priority
        )
    }

    fun toResponseDto(request: EmployeeRequest): RequestResponseDTO {
        return RequestResponseDTO(
            id = request.id!!,
            requestId = request.requestId,
            employeeId = request.employee.employeeId,
            employeeName = "${request.employee.firstName} ${request.employee.lastName}",
            department = request.employee.department,
            requestType = request.requestType,
            title = request.title,
            description = request.description,
            startDate = request.startDate,
            endDate = request.endDate,
            numberOfDays = request.numberOfDays,
            amount = request.amount,
            currency = request.currency,
            justification = request.justification,
            status = request.status,
            createdAt = request.createdAt,
            processedAt = request.processedAt,
            processedByName = request.processedBy?.let { "${it.firstName} ${it.lastName}" },
            approverComments = request.approverComments,
            priority = request.priority,
            attachmentPath = request.attachmentPath
        )
    }
}