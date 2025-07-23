// File: EmployeeService.kt
package com.example.employee_management.service

import com.example.employee_management.model.Employee
import com.example.employee_management.repository.EmployeeRepository
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository
) {

    fun getAllEmployees(): List<Employee> = employeeRepository.findAll()

    fun getActiveEmployeeById(id: Long): Employee {
        return employeeRepository.findByIdAndActiveTrue(id)
            ?: throw RuntimeException("Active employee not found with id $id")
    }

    fun getActiveEmployeeByEmployeeId(uuid: UUID): Employee {
        return employeeRepository.findByEmployeeIdAndActiveTrue(uuid)
            ?: throw RuntimeException("Active employee not found with UUID $uuid")
    }

    fun getActiveEmployeeByWorkEmail(email: String): Employee =
        employeeRepository.findByWorkEmailAndActiveTrue(email)
            ?: throw RuntimeException("Active employee with email $email not found")


    fun createEmployee(employee: Employee): Employee {
        val existing = employeeRepository.findByWorkEmail(employee.workEmail)

        return if (existing != null) {
            if (existing.active) {
                throw IllegalArgumentException("Work email already exists")
            } else {
                // Reactivate and update the existing employee
                val updatedEmployee = employee.copy(
                    id = existing.id,
                    employeeId = existing.employeeId,
                    microsoftId = existing.microsoftId,
                    active = true
                )
                employeeRepository.save(updatedEmployee)
            }
        } else {
            employeeRepository.save(employee)
        }
    }


    fun updateEmployee(id: Long, updated: Employee): Employee {
        val existing = employeeRepository.findByIdAndActiveTrue(id)
            ?: throw RuntimeException("Active employee not found")

        // You can later add mapper logic instead of this direct override
        val updatedEmployee = updated.copy(id = existing.id, employeeId = existing.employeeId)
        return employeeRepository.save(updatedEmployee)
    }

    fun deleteEmployee(id: Long) {
        if (!employeeRepository.existsById(id)) {
            throw RuntimeException("Employee not found")
        }
        employeeRepository.deleteById(id)
    }

    // ✅ Upload profile photo
    fun uploadPhoto(uuid: UUID, file: MultipartFile): String {
        val employee = getActiveEmployeeByEmployeeId(uuid)
        val fileName = "photo_${employee.id}_${file.originalFilename}"
        val filePath = Paths.get("uploads/photos", fileName)
        Files.createDirectories(filePath.parent)
        Files.write(filePath, file.bytes)
        employee.photoPath = filePath.toString()
        employeeRepository.save(employee)
        return fileName
    }

    // ✅ Retrieve profile photo
    fun getPhoto(uuid: UUID): Pair<ByteArray, String> {
        val employee = getActiveEmployeeByEmployeeId(uuid)
        val path = Paths.get(employee.photoPath ?: throw NoSuchElementException("No photo found"))
        return Files.readAllBytes(path) to Files.probeContentType(path)
    }

    fun uploadResume(uuid: UUID, file: MultipartFile): String {
        val employee = getActiveEmployeeByEmployeeId(uuid)
        val fileName = "resume_${employee.id}_${file.originalFilename}"
        val filePath = Paths.get("uploads/resumes", fileName)
        Files.createDirectories(filePath.parent)
        Files.write(filePath, file.bytes)
        employee.resumePath = filePath.toString()
        employeeRepository.save(employee)
        return fileName
    }

    fun getResume(uuid: UUID): Pair<ByteArray, String> {
        val employee = getActiveEmployeeByEmployeeId(uuid)
        val path = Paths.get(employee.resumePath ?: throw NoSuchElementException("No resume found"))
        return Files.readAllBytes(path) to Files.probeContentType(path)
    }

    fun deactivateEmployee(id: Long) {
        val employee = employeeRepository.findById(id)
            .orElseThrow { RuntimeException("Employee not found with id $id") }

        employee.active = false
        employeeRepository.save(employee)
    }

    fun getAllActiveEmployees(): List<Employee> {
        return employeeRepository.findAllByActiveTrue()
    }


}
