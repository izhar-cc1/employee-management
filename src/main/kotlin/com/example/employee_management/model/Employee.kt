// File: Employee.kt
package com.example.employee_management.model

import jakarta.persistence.*
import jakarta.validation.constraints.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "employees")
data class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true)
    val employeeId: UUID = UUID.randomUUID(),

    @Column(unique = true)
    var microsoftId: String? = null,

    @NotBlank
    var firstName: String,

    @NotBlank
    var lastName: String,

    var middleName: String? = null,

    @Past
    var dateOfBirth: LocalDate,

    var gender: String? = null,
    var nationality: String? = null,
    var maritalStatus: String? = null,
    var photoPath: String? = null,

    @Email
    @Column(unique = true)
    var workEmail: String,

    @Email
    var personalEmail: String? = null,

    @NotBlank
    var mobileNumber: String,

    var alternatePhoneNumber: String? = null,
    @Column(columnDefinition = "TEXT")
    var currentAddress: String? = null,
    @Column(columnDefinition = "TEXT")
    var permanentAddress: String? = null,
    var emergencyContactName: String? = null,
    var emergencyContactNumber: String? = null,

    @NotBlank
    var jobTitle: String,

    @NotBlank
    var department: String,

    var managerName: String? = null,

    @PastOrPresent
    var dateOfJoining: LocalDate,

    @Enumerated(EnumType.STRING)
    var employmentType: EmploymentType,

    @Enumerated(EnumType.STRING)
    var employeeStatus: EmployeeStatus = EmployeeStatus.ACTIVE,

    @Enumerated(EnumType.STRING)
    var workMode: WorkMode,

    var officeLocation: String? = null,

    @NotBlank
    @Column(nullable = false, unique = true)
    var employeeCode: String,

    @Column(columnDefinition = "TEXT")
    var skills: String? = null,
    @Column(columnDefinition = "TEXT")
    var certifications: String? = null,
    @Column(columnDefinition = "TEXT")
    var education: String? = null,
    @Column(columnDefinition = "TEXT")
    var previousExperience: String? = null,
    @Column(columnDefinition = "TEXT")
    var projects: String? = null,
    @Column(columnDefinition = "TEXT")
    var languagesKnown: String? = null,
    var linkedInUrl: String? = null,
    var githubUrl: String? = null,
    var portfolioUrl: String? = null,

    var salary: Double? = null,
    var bankAccountNumber: String? = null,
    var panNumber: String? = null,
    var pfNumber: String? = null,
    @Column(columnDefinition = "TEXT")
    var insuranceDetails: String? = null,
    var bonusAmount: Double? = null,

    @Column(columnDefinition = "TEXT")
    var attendanceRecord: String? = null,
    var leaveBalance: Int? = null,
    @Column(columnDefinition = "TEXT")
    var leaveHistory: String? = null,
    var shiftTiming: String? = null,

    var performanceRating: Double? = null,
    @Column(columnDefinition = "TEXT")
    var appraisalHistory: String? = null,
    @Column(columnDefinition = "TEXT")
    var goals: String? = null,
    @Column(columnDefinition = "TEXT")
    var trainingsAttended: String? = null,

    var resumePath: String? = null,
    var offerLetterPath: String? = null,
    var experienceLettersPath: String? = null,
    var idProofPath: String? = null,
    var addressProofPath: String? = null,

    var systemUsername: String? = null,
    var systemRole: String = "EMPLOYEE",
    var badgeId: String? = null,

    var bloodGroup: String? = null,
    var tShirtSize: String? = null,
    @Column(columnDefinition = "TEXT")
    var hobbies: String? = null,
    @Column(columnDefinition = "TEXT")
    var vehicleDetails: String? = null,
    @Column(columnDefinition = "TEXT")
    var remarks: String? = null,

    @NotBlank
    @Column(nullable = false, unique = true)
    var aadhaarNumber: String,

    @Column(nullable = false)
    var active: Boolean = true
)
