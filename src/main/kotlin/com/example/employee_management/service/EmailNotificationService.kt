// File: EmailNotificationService.kt (Optional)
package com.example.employee_management.service

import org.springframework.stereotype.Service
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import com.example.employee_management.model.EmployeeRequest
import com.example.employee_management.model.Employee

@Service
class EmailNotificationService(
    private val mailSender: JavaMailSender
) {

    fun sendRequestSubmittedNotification(request: EmployeeRequest) {
        val message = SimpleMailMessage()
        message.setTo(request.employee.workEmail)
        message.setSubject("Request Submitted - ${request.title}")
        message.setText("""
            Dear ${request.employee.firstName},
            
            Your request has been submitted successfully.
            
            Request Details:
            - Type: ${request.requestType}
            - Title: ${request.title}
            - Status: ${request.status}
            - Request ID: ${request.requestId}
            
            You will be notified once your request is processed.
            
            Best regards,
            Employee Management System
        """.trimIndent())

        mailSender.send(message)
    }

    fun sendRequestProcessedNotification(request: EmployeeRequest) {
        val message = SimpleMailMessage()
        message.setTo(request.employee.workEmail)
        message.setSubject("Request ${request.status} - ${request.title}")
        message.setText("""
            Dear ${request.employee.firstName},
            
            Your request has been ${request.status.name.lowercase()}.
            
            Request Details:
            - Type: ${request.requestType}
            - Title: ${request.title}
            - Status: ${request.status}
            - Processed by: ${request.processedBy?.firstName} ${request.processedBy?.lastName}
            - Comments: ${request.approverComments ?: "No comments"}
            
            Best regards,
            Employee Management System
        """.trimIndent())

        mailSender.send(message)
    }

    fun sendApprovalNotification(request: EmployeeRequest, approvers: List<Employee>) {
        approvers.forEach { approver ->
            val message = SimpleMailMessage()
            message.setTo(approver.workEmail)
            message.setSubject("New Request for Approval - ${request.title}")
            message.setText("""
                Dear ${approver.firstName},
                
                A new request requires your approval.
                
                Request Details:
                - Employee: ${request.employee.firstName} ${request.employee.lastName}
                - Department: ${request.employee.department}
                - Type: ${request.requestType}
                - Title: ${request.title}
                - Priority: ${request.priority}
                - Request ID: ${request.requestId}
                
                Please review and process this request.
                
                Best regards,
                Employee Management System
            """.trimIndent())

            mailSender.send(message)
        }
    }
}