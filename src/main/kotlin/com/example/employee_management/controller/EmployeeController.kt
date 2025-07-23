// File: EmployeeController.kt
package com.example.employee_management.controller

import com.example.employee_management.dto.EmployeeRequestDTO
import com.example.employee_management.dto.EmployeeResponseDTO
import com.example.employee_management.mapper.EmployeeMapper
import com.example.employee_management.service.EmployeeService
import com.example.employee_management.util.JwtTokenUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/employees")
class EmployeeController(
    private val employeeService: EmployeeService,
    private val jwtTokenUtil: JwtTokenUtil,
    private val employeeMapper: EmployeeMapper
) {
    // ✅ Public - Welcome endpoint
    @GetMapping("/welcome")
    fun welcome(): ResponseEntity<String> {
        return ResponseEntity.ok("Welcome to the Employee Management System!")
    }

    // ✅ Public - Get all active employees
    @GetMapping
    fun getAllActiveEmployees(): ResponseEntity<List<EmployeeResponseDTO>> {
        val activeEmployees = employeeService.getAllActiveEmployees() // fetch only active ones
        val response = activeEmployees.map { employeeMapper.toResponseDto(it) }
        return ResponseEntity.ok(response)
    }


    // ✅ Public - Get active employee by database ID
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<EmployeeResponseDTO> {
        val employee = employeeService.getActiveEmployeeById(id)
        return ResponseEntity.ok(employeeMapper.toResponseDto(employee))
    }

    // ✅ Public - Get active employee by UUID
    @GetMapping("/uuid/{uuid}")
    fun getByEmployeeId(@PathVariable uuid: UUID): ResponseEntity<EmployeeResponseDTO> {
        val employee = employeeService.getActiveEmployeeByEmployeeId(uuid)
        return ResponseEntity.ok(employeeMapper.toResponseDto(employee))
    }

    @PostMapping("/{id}/photo")
    fun uploadPhoto(
        @PathVariable id: Long,
        @RequestParam("file") file: MultipartFile,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<String> {
        enforceAdmin(authHeader)
        val fileName = employeeService.uploadPhoto(id, file)
        return ResponseEntity.ok(fileName)
    }

    @GetMapping("/{id}/photo")
    fun getPhoto(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val (bytes, contentType) = employeeService.getPhoto(id)
        return ResponseEntity.ok()
            .header("Content-Type", contentType)
            .body(bytes)
    }

    @PostMapping("/{id}/resume")
    fun uploadResume(
        @PathVariable id: Long,
        @RequestParam("file") file: MultipartFile,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<String> {
        enforceAdmin(authHeader)
        val fileName = employeeService.uploadResume(id, file)
        return ResponseEntity.ok(fileName)
    }

    @GetMapping("/{id}/resume")
    fun getResume(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val (bytes, contentType) = employeeService.getResume(id)
        return ResponseEntity.ok()
            .header("Content-Type", contentType)
            .header("Content-Disposition", "attachment; filename=resume.pdf")
            .body(bytes)
    }


    // 🔒 Admin only - Create
    @PostMapping
    fun createEmployee(
        @RequestBody dto: EmployeeRequestDTO,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<EmployeeResponseDTO> {
        enforceAdmin(authHeader)
        val employee = employeeMapper.toEntity(dto)
        val saved = employeeService.createEmployee(employee)
        return ResponseEntity.ok(employeeMapper.toResponseDto(saved))
    }

    // 🔒 Admin only - Update
    @PutMapping("/{id}")
    fun updateEmployee(
        @PathVariable id: Long,
        @RequestBody dto: EmployeeRequestDTO,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<EmployeeResponseDTO> {
        enforceAdmin(authHeader)
        val employee = employeeMapper.toEntity(dto)
        val updated = employeeService.updateEmployee(id, employee)
        return ResponseEntity.ok(employeeMapper.toResponseDto(updated))
    }

    // 🔒 Admin only - Delete
//    @DeleteMapping("/{id}")
//    fun deleteEmployee(
//        @PathVariable id: Long,
//        @RequestHeader("Authorization") authHeader: String
//    ): ResponseEntity<Void> {
//        enforceAdmin(authHeader)
//        employeeService.deleteEmployee(id)
//        return ResponseEntity.noContent().build()
//    }

    // 🔒 Admin only - Delete
    @DeleteMapping("/{id}")
    fun deactivateEmployee(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Void> {
        enforceAdmin(authHeader)
        employeeService.deactivateEmployee(id)
        return ResponseEntity.noContent().build()
    }

    // ✅ Public - Validate token and return employee by work email
    @GetMapping("/validate-token")
    fun validateToken(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<EmployeeResponseDTO> {
        val token = authHeader.removePrefix("Bearer ").trim()

        // Extract email from token
        val email = try {
            jwtTokenUtil.extractEmail(token)
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token")
        }

        // Check if employee exists with this work email and is active
        val employee = employeeService.getActiveEmployeeByWorkEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No active employee found for email: $email")

        return ResponseEntity.ok(employeeMapper.toResponseDto(employee))
    }


    // ✅ Extract Bearer token & check systemRole
    private fun enforceAdmin(authHeader: String) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val role = jwtTokenUtil.extractRole(token)
        if (role != "ADMIN") {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMINs can perform this operation")
        }
    }
}
