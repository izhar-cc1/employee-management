// File: RequestService.kt
package com.example.employee_management.service

import com.example.employee_management.dto.CreateRequestDTO
import com.example.employee_management.dto.ProcessRequestDTO
import com.example.employee_management.model.Employee
import com.example.employee_management.model.EmployeeRequest
import com.example.employee_management.model.RequestStatus
import com.example.employee_management.model.RequestType
import com.example.employee_management.repository.EmployeeRepository
import com.example.employee_management.repository.RequestRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

@Service
class RequestService(
    private val requestRepository: RequestRepository,
    private val employeeService: EmployeeService,
    private val employeeRepository: EmployeeRepository
) {

    fun createRequest(employeeId: UUID, dto: CreateRequestDTO): EmployeeRequest {
        val employee = employeeService.getActiveEmployeeByEmployeeId(employeeId)

        val request = EmployeeRequest(
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

        return requestRepository.save(request)
    }

    fun getRequestById(requestId: UUID): EmployeeRequest {
        return requestRepository.findByRequestIdAndActiveTrue(requestId)
            ?: throw RuntimeException("Request not found with ID: $requestId")
    }

    fun getEmployeeRequests(employeeId: UUID): List<EmployeeRequest> {
        return requestRepository.findByEmployeeEmployeeIdAndActiveTrue(employeeId)
    }

    fun getPendingRequestsForDepartment(department: String): List<EmployeeRequest> {
        return requestRepository.findByDepartmentAndStatus(department, RequestStatus.PENDING)
    }

    fun getAllPendingRequests(): List<EmployeeRequest> {
        return requestRepository.findByStatusAndActiveTrue(RequestStatus.PENDING)
    }

    fun getRequestsByType(requestType: RequestType): List<EmployeeRequest> {
        return requestRepository.findByRequestTypeAndActiveTrue(requestType)
    }

    fun processRequest(requestId: UUID, approverId: UUID, dto: ProcessRequestDTO): EmployeeRequest {
        val request = getRequestById(requestId)
        val approver = employeeService.getActiveEmployeeByEmployeeId(approverId)

        // Check if approver has permission to process this request
        if (!canApproveRequest(approver.department, approver.systemRole, request)) {
            throw RuntimeException("You don't have permission to process this request")
        }

        // ✅ If it's a leave request and being approved, subtract leave balance
        if (request.requestType == RequestType.LEAVE &&
            dto.status == RequestStatus.APPROVED &&
            request.numberOfDays != null
        ) {
            val employee = request.employee
            if (employee.leaveBalance < request.numberOfDays) {
                throw RuntimeException("Not enough leave balance")
            }

            // Subtract leave days
            employee.leaveBalance -= request.numberOfDays
            employeeRepository.save(employee) // 💾 Save the updated employee
        }

        request.status = dto.status
        request.processedAt = LocalDateTime.now()
        request.processedBy = approver
        request.approverComments = dto.approverComments

        return requestRepository.save(request)
    }

    fun uploadAttachment(requestId: UUID, file: MultipartFile): String {
        val request = getRequestById(requestId)
        val fileName = "request_${request.id}_${file.originalFilename}"
        val filePath = Paths.get("uploads/requests", fileName)
        Files.createDirectories(filePath.parent)
        Files.write(filePath, file.bytes)

        request.attachmentPath = filePath.toString()
        requestRepository.save(request)
        return fileName
    }

    fun getAttachment(requestId: UUID): Pair<ByteArray, String> {
        val request = getRequestById(requestId)
        val path = Paths.get(request.attachmentPath ?: throw NoSuchElementException("No attachment found"))
        return Files.readAllBytes(path) to Files.probeContentType(path)
    }

    fun cancelRequest(requestId: UUID, employeeId: UUID): EmployeeRequest {
        val request = getRequestById(requestId)

        // Only allow cancellation by the request owner and only if it's pending
        if (request.employee.employeeId != employeeId) {
            throw RuntimeException("You can only cancel your own requests")
        }

        if (request.status != RequestStatus.PENDING) {
            throw RuntimeException("Only pending requests can be cancelled")
        }

        request.status = RequestStatus.CANCELLED
        request.processedAt = LocalDateTime.now()

        return requestRepository.save(request)
    }

    fun deleteRequest(requestId: UUID) {
        val request = getRequestById(requestId)
        request.active = false
        requestRepository.save(request)
    }

    private fun canApproveRequest(approverDepartment: String, approverRole: String, request: EmployeeRequest): Boolean {
        // Admin can approve any request
        if (approverRole == "ADMIN") {
            return true
        }

        // Department-specific approval logic
        return when (request.requestType) {
            RequestType.LEAVE -> {
                // HR can approve leave requests, or department heads can approve their own department's requests
                approverDepartment == "HR" ||
                        (approverDepartment == request.employee.department && approverRole == "MANAGER")
            }
            RequestType.EQUIPMENT -> {
                // IT can approve equipment requests
                approverDepartment == "IT" || approverDepartment == "ADMIN"
            }
            RequestType.TRAVEL -> {
                // HR or Admin can approve travel requests
                approverDepartment == "HR" || approverDepartment == "ADMIN"
            }
            RequestType.REIMBURSEMENT -> {
                // Finance can approve reimbursement requests
                approverDepartment == "FINANCE" || approverDepartment == "HR"
            }
            RequestType.TRAINING -> {
                // HR can approve training requests
                approverDepartment == "HR"
            }
            RequestType.GENERAL -> {
                // HR or immediate department head can approve general requests
                approverDepartment == "HR" ||
                        (approverDepartment == request.employee.department && approverRole == "MANAGER")
            }
        }
    }

    fun getApproversForRequest(request: EmployeeRequest): List<Employee> {
        return when (request.requestType) {
            RequestType.LEAVE -> {
                // Get HR employees + department managers
                val hrEmployees = employeeRepository.findByDepartmentAndActiveTrue("HR")
                val departmentManagers = employeeRepository.findByDepartmentAndSystemRoleAndActiveTrue(
                    request.employee.department, "MANAGER"
                )
                val admins = employeeRepository.findBySystemRoleAndActiveTrue("ADMIN")
                (hrEmployees + departmentManagers + admins).distinctBy { it.id }
            }

            RequestType.EQUIPMENT -> {
                // Get IT employees + admins
                val itEmployees = employeeRepository.findByDepartmentAndActiveTrue("IT")
                val admins = employeeRepository.findBySystemRoleAndActiveTrue("ADMIN")
                (itEmployees + admins).distinctBy { it.id }
            }

            RequestType.TRAVEL -> {
                // Get HR employees + admins
                val hrEmployees = employeeRepository.findByDepartmentAndActiveTrue("HR")
                val admins = employeeRepository.findBySystemRoleAndActiveTrue("ADMIN")
                (hrEmployees + admins).distinctBy { it.id }
            }

            RequestType.REIMBURSEMENT -> {
                // Get Finance + HR employees
                val financeEmployees = employeeRepository.findByDepartmentAndActiveTrue("FINANCE")
                val hrEmployees = employeeRepository.findByDepartmentAndActiveTrue("HR")
                val admins = employeeRepository.findBySystemRoleAndActiveTrue("ADMIN")
                (financeEmployees + hrEmployees + admins).distinctBy { it.id }
            }

            RequestType.TRAINING -> {
                // Get HR employees
                val hrEmployees = employeeRepository.findByDepartmentAndActiveTrue("HR")
                val admins = employeeRepository.findBySystemRoleAndActiveTrue("ADMIN")
                (hrEmployees + admins).distinctBy { it.id }
            }

            RequestType.GENERAL -> {
                // Get HR employees + department managers
                val hrEmployees = employeeRepository.findByDepartmentAndActiveTrue("HR")
                val departmentManagers = employeeRepository.findByDepartmentAndSystemRoleAndActiveTrue(
                    request.employee.department, "MANAGER"
                )
                val admins = employeeRepository.findBySystemRoleAndActiveTrue("ADMIN")
                (hrEmployees + departmentManagers + admins).distinctBy { it.id }
            }
        }
    }
}