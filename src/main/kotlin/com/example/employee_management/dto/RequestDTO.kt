// File: RequestDTO.kt
package com.example.employee_management.dto

import com.example.employee_management.model.RequestType
import com.example.employee_management.model.RequestStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class CreateRequestDTO(
    val requestType: RequestType,
    val title: String,
    val description: String,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val numberOfDays: Int? = null,
    val amount: Double? = null,
    val currency: String? = "INR",
    val justification: String? = null,
    val priority: String = "MEDIUM"
)

data class ProcessRequestDTO(
    val status: RequestStatus,
    val approverComments: String? = null
)

data class RequestResponseDTO(
    val id: Long,
    val requestId: UUID,
    val employeeId: UUID,
    val employeeName: String,
    val department: String,
    val requestType: RequestType,
    val title: String,
    val description: String,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val numberOfDays: Int? = null,
    val amount: Double? = null,
    val currency: String? = null,
    val justification: String? = null,
    val status: RequestStatus,
    val createdAt: LocalDateTime,
    val processedAt: LocalDateTime? = null,
    val processedByName: String? = null,
    val approverComments: String? = null,
    val priority: String,
    val attachmentPath: String? = null
)