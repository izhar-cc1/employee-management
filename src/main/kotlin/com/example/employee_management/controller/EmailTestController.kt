package com.example.employee_management.controller

import com.example.employee_management.service.EmailNotificationService
import org.springframework.web.bind.annotation.*
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.beans.factory.annotation.Value

@RestController
@RequestMapping("/test")
class EmailTestController(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val fromEmail: String
) {

    @PostMapping("/send-email")
    fun testEmail(@RequestParam email: String = "izhar@correctcloud.io"): String {
        return try {
            val message = SimpleMailMessage()
            message.setFrom(fromEmail)
            message.setTo(email)
            message.setSubject("🧪 Test Email from Employee Management System")
            message.setText("""
                Hello!
                
                This is a test email to verify that our Employee Management System 
                can successfully send notifications to your work email address.
                
                If you receive this email, the integration is working correctly! ✅
                
                Best regards,
                Employee Management System
            """.trimIndent())

            mailSender.send(message)
            "✅ Test email sent successfully to $email"
        } catch (e: Exception) {
            "❌ Failed to send email: ${e.message}"
        }
    }
}