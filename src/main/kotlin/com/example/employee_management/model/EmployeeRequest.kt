// File: EmployeeRequest.kt
package com.example.employee_management.model

import jakarta.persistence.*
import jakarta.validation.constraints.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "employee_requests")
data class EmployeeRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true)
    val requestId: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    val employee: Employee,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val requestType: RequestType,

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT")
    val description: String,

    // For leave requests
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val numberOfDays: Int? = null,

    // For equipment/reimbursement requests
    val amount: Double? = null,
    val currency: String? = "INR",

    @Column(columnDefinition = "TEXT")
    val justification: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: RequestStatus = RequestStatus.PENDING,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    var processedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    var processedBy: Employee? = null,

    @Column(columnDefinition = "TEXT")
    var approverComments: String? = null,

    @Column(columnDefinition = "TEXT")
    var attachmentPath: String? = null,

    // Priority level
    @Column(nullable = false)
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW

    @Column(nullable = false)
    var active: Boolean = true
)