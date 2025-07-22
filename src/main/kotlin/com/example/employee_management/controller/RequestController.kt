// File: RequestController.kt
package com.example.employee_management.controller

import com.example.employee_management.dto.CreateRequestDTO
import com.example.employee_management.dto.ProcessRequestDTO
import com.example.employee_management.dto.RequestResponseDTO
import com.example.employee_management.model.RequestType
import com.example.employee_management.service.EmailNotificationService
import com.example.employee_management.service.RequestService
import com.example.employee_management.service.EmployeeService
import com.example.employee_management.util.JwtTokenUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/requests")
class RequestController(
    private val requestService: RequestService,
    private val employeeService: EmployeeService,
    private val emailNotificationService: EmailNotificationService,
    private val jwtTokenUtil: JwtTokenUtil
) {

    // Create a new request
    @PostMapping
    fun createRequest(
        @RequestBody dto: CreateRequestDTO,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<RequestResponseDTO> {
        val employeeId = extractEmployeeId(authHeader)
        val request = requestService.createRequest(employeeId, dto)

        // Send email notifications
        try {
            // 1. Send confirmation to employee
            emailNotificationService.sendRequestSubmittedNotification(request)

            // 2. Send notification to approvers
            val approvers = requestService.getApproversForRequest(request)
            if (approvers.isNotEmpty()) {
                emailNotificationService.sendApprovalNotification(request, approvers)
            }
        } catch (e: Exception) {
            // Log email failure but don't fail the request creation
            println("Failed to send email notifications: ${e.message}")
        }
        return ResponseEntity.ok(toResponseDTO(request))
    }

    // Get all requests for the authenticated employee
    @GetMapping("/my-requests")
    fun getMyRequests(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<List<RequestResponseDTO>> {
        val employeeId = extractEmployeeId(authHeader)
        val requests = requestService.getEmployeeRequests(employeeId)
        return ResponseEntity.ok(requests.map { toResponseDTO(it) })
    }

    // Get a specific request by ID
    @GetMapping("/{requestId}")
    fun getRequest(
        @PathVariable requestId: UUID,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<RequestResponseDTO> {
        val request = requestService.getRequestById(requestId)
        val employeeId = extractEmployeeId(authHeader)
        val employee = employeeService.getActiveEmployeeByEmployeeId(employeeId)

        // Check if user can view this request
        if (request.employee.employeeId != employeeId &&
            employee.systemRole != "ADMIN" &&
            employee.department != "HR") {
            throw RuntimeException("Access denied")
        }

        return ResponseEntity.ok(toResponseDTO(request))
    }

    // Get pending requests for approval (department-specific)
    @GetMapping("/pending")
    fun getPendingRequests(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam(required = false) department: String?
    ): ResponseEntity<List<RequestResponseDTO>> {
        val employeeId = extractEmployeeId(authHeader)
        val employee = employeeService.getActiveEmployeeByEmployeeId(employeeId)

        val requests = if (employee.systemRole == "ADMIN") {
            requestService.getAllPendingRequests()
        } else {
            val targetDepartment = department ?: employee.department
            requestService.getPendingRequestsForDepartment(targetDepartment)
        }

        return ResponseEntity.ok(requests.map { toResponseDTO(it) })
    }

    // Process a request (approve/reject)
    @PutMapping("/{requestId}/process")
    fun processRequest(
        @PathVariable requestId: UUID,
        @RequestBody dto: ProcessRequestDTO,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<RequestResponseDTO> {
        val approverId = extractEmployeeId(authHeader)
        val request = requestService.processRequest(requestId, approverId, dto)

        // Send email notification to employee about the decision
        try {
            emailNotificationService.sendRequestProcessedNotification(request)
        } catch (e: Exception) {
            println("Failed to send processing notification: ${e.message}")
        }
        return ResponseEntity.ok(toResponseDTO(request))
    }

    // Cancel a request
    @PutMapping("/{requestId}/cancel")
    fun cancelRequest(
        @PathVariable requestId: UUID,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<RequestResponseDTO> {
        val employeeId = extractEmployeeId(authHeader)
        val request = requestService.cancelRequest(requestId, employeeId)
        return ResponseEntity.ok(toResponseDTO(request))
    }

    // Upload attachment to a request
    @PostMapping("/{requestId}/attachment")
    fun uploadAttachment(
        @PathVariable requestId: UUID,
        @RequestParam("file") file: MultipartFile,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<String> {
        val employeeId = extractEmployeeId(authHeader)
        val request = requestService.getRequestById(requestId)

        // Only allow the request owner to upload attachments
        if (request.employee.employeeId != employeeId) {
            throw RuntimeException("You can only upload attachments to your own requests")
        }

        val fileName = requestService.uploadAttachment(requestId, file)
        return ResponseEntity.ok(fileName)
    }

    // Get attachment from a request
    @GetMapping("/{requestId}/attachment")
    fun getAttachment(
        @PathVariable requestId: UUID,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<ByteArray> {
        val (bytes, contentType) = requestService.getAttachment(requestId)
        return ResponseEntity.ok()
            .header("Content-Type", contentType)
            .header("Content-Disposition", "attachment")
            .body(bytes)
    }

    // Get requests by type (for reporting)
    @GetMapping("/type/{requestType}")
    fun getRequestsByType(
        @PathVariable requestType: RequestType,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<List<RequestResponseDTO>> {
        val employeeId = extractEmployeeId(authHeader)
        val employee = employeeService.getActiveEmployeeByEmployeeId(employeeId)

        // Only admins and HR can view requests by type
        if (employee.systemRole != "ADMIN" && employee.department != "HR") {
            throw RuntimeException("Access denied")
        }

        val requests = requestService.getRequestsByType(requestType)
        return ResponseEntity.ok(requests.map { toResponseDTO(it) })
    }

    // Delete a request (soft delete)
    @DeleteMapping("/{requestId}")
    fun deleteRequest(
        @PathVariable requestId: UUID,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Void> {
        val employeeId = extractEmployeeId(authHeader)
        val employee = employeeService.getActiveEmployeeByEmployeeId(employeeId)

        // Only admins can delete requests
        if (employee.systemRole != "ADMIN") {
            throw RuntimeException("Only admins can delete requests")
        }

        requestService.deleteRequest(requestId)
        return ResponseEntity.noContent().build()
    }

    private fun extractEmployeeId(authHeader: String): UUID {
        val token = authHeader.removePrefix("Bearer ").trim()
        val email = jwtTokenUtil.extractEmail(token)
        val employee = employeeService.getActiveEmployeeByWorkEmail(email)
        return employee.employeeId
    }

    private fun toResponseDTO(request: com.example.employee_management.model.EmployeeRequest): RequestResponseDTO {
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