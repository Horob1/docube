package com.horob1.common_service.api.exception

import com.horob1.common_service.api.dto.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * Xử lý các lỗi nghiệp vụ tùy chỉnh của ứng dụng.
     * Đây là handler quan trọng nhất cho các lỗi có thể dự đoán được.
     */
    @ExceptionHandler(AppException::class)
    fun handleAppException(e: AppException): ResponseEntity<ApiResponse<Nothing>> {
        val error = e.appError
        logger.error("AppException occurred: code={}, message='{}'", error.code, error.message, e)

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            message = error.message,
            code = error.code
        )
        return ResponseEntity.status(error.status).body(response)
    }

    /**
     * Xử lý lỗi validation từ @Valid.
     * Trả về thông báo lỗi chi tiết cho từng trường.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.ValidationError
        val errorMessage = e.bindingResult.fieldErrors
            .joinToString("; ") { "'${it.field}': ${it.defaultMessage}" }

        logger.warn("Validation error: {}", errorMessage)

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            message = errorMessage, // Ghi đè message mặc định với thông tin chi tiết
            code = error.code
        )
        return ResponseEntity.status(error.status).body(response)
    }

    /**
     * Xử lý lỗi 403 Forbidden khi user không có quyền truy cập.
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.AccessForbidden
        logger.warn("Access denied: {}", e.message)

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            code = error.code,
            message = error.message
        )
        return ResponseEntity.status(error.status).body(response)
    }

    /**
     * Xử lý lỗi 401 Unauthorized khi xác thực thất bại.
     */
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.AuthenticationFailed
        logger.warn("Authentication failed: {}", e.message)

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            code = error.code,
            message = error.message
        )
        return ResponseEntity.status(error.status).body(response)
    }

    /**
     * Xử lý lỗi 405 Method Not Allowed.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(e: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.MethodNotAllowed
        val message =
            "Method '${e.method}' is not supported for this route. Supported methods are ${e.supportedHttpMethods}."
        logger.warn(message)

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            code = error.code,
            message = message
        )
        return ResponseEntity.status(error.status).body(response)
    }

    /**
     * Xử lý lỗi 404 Not Found.
     * Cần cấu hình: spring.mvc.throw-exception-if-no-handler-found=true
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFound(e: NoHandlerFoundException): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.NotFound
        logger.warn("No handler found for {} {}", e.httpMethod, e.requestURL)
        val response = ApiResponse.error<Nothing>(
            status = error.status,
            code = error.code,
            message = "API route not found: ${e.requestURL}"
        )
        return ResponseEntity.status(error.status).body(response)
    }

    /**
     * Chốt chặn cuối cùng, xử lý tất cả các lỗi không lường trước (500 Internal Server Error).
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.InternalServerError
        logger.error("An unexpected error occurred", e) // Log đầy đủ stack trace cho lỗi không mong muốn

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            message = error.message,
            code = error.code
        )
        return ResponseEntity.status(error.status).body(response)
    }
}