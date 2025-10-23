package com.horob1.notification_service.infrastructure.service

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.context.Context


@Service
class EmailService(
    private val emailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine
) {
    /**
     * Gửi email OTP cho người dùng
     *
     * @param email Email người nhận
     * @param otp Mã OTP
     * @param recipientName Tên người nhận (tùy chọn)
     * @param appName Tên hệ thống
     * @param expirationTime Thời gian hết hạn (mặc định "15 minutes")
     * @param title Tiêu đề email (tùy chọn)
     */
    fun sendOtpEmail(
        email: String,
        otp: String,
        recipientName: String? = null,
        appName: String = "Your System",
        expirationTime: String = "15 minutes",
        title: String? = null
    ) {
        try {
            // Tạo context cho Thymeleaf
            val context = Context().apply {
                setVariable("otp", otp)
                setVariable("appName", appName)
                setVariable("expirationTime", expirationTime)
                setVariable("recipientName", recipientName)
                setVariable("title", title)
            }

            val htmlContent = templateEngine.process("email/otp_template", context)

            val message: MimeMessage = emailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setTo(email)
            helper.setSubject(title ?: "Your OTP Code")
            helper.setText(htmlContent, true)

            helper.setFrom("noreply@yoursystem.com")

            emailSender.send(message)


        } catch (e: Exception) {

        }
    }
}