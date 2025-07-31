// File: RequestRepository.kt
package com.example.employee_management.repository

import com.example.employee_management.model.EmployeeRequest
import com.example.employee_management.model.RequestStatus
import com.example.employee_management.model.RequestType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RequestRepository : JpaRepository<EmployeeRequest, Long> {

    fun findByRequestIdAndActiveTrue(requestId: UUID): EmployeeRequest?

    fun findByEmployeeIdAndActiveTrue(employeeId: Long): List<EmployeeRequest>

    fun findByEmployeeEmployeeIdAndActiveTrue(employeeId: UUID): List<EmployeeRequest>

    fun findByStatusAndActiveTrue(status: RequestStatus): List<EmployeeRequest>

    fun findByRequestTypeAndActiveTrue(requestType: RequestType): List<EmployeeRequest>

    @Query("SELECT r FROM EmployeeRequest r WHERE r.employee.department = :department AND r.status = :status AND r.active = true")
    fun findByDepartmentAndStatus(@Param("department") department: String, @Param("status") status: RequestStatus): List<EmployeeRequest>

    @Query("SELECT r FROM EmployeeRequest r WHERE r.employee.department = :department AND r.active = true")
    fun findByDepartment(@Param("department") department: String): List<EmployeeRequest>

    fun findAllByActiveTrue(): List<EmployeeRequest>

    @Query("SELECT r FROM EmployeeRequest r WHERE r.processedBy.id = :approverId AND r.active = true")
    fun findByProcessedBy(@Param("approverId") approverId: Long): List<EmployeeRequest>

    // Method to find requests by multiple statuses
    fun findByStatusInAndActiveTrue(statuses: List<RequestStatus>): List<EmployeeRequest>

}