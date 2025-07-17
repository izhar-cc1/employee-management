// File: EmployeeRepository.kt
package com.example.employee_management.repository

import com.example.employee_management.model.Employee
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EmployeeRepository : JpaRepository<Employee, Long> {
    fun existsByWorkEmail(workEmail: String): Boolean
    fun findAllByActiveTrue(): List<Employee>
    fun findByIdAndActiveTrue(id: Long): Employee?
    fun findByEmployeeIdAndActiveTrue(employeeId: UUID): Employee?
    fun findByWorkEmailAndActiveTrue(email: String): Employee?
    fun findByWorkEmail(email: String): Employee?

}
