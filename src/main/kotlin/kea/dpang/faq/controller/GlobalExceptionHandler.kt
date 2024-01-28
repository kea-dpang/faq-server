package kea.dpang.faq.controller

import kea.dpang.faq.base.ErrorResponse
import kea.dpang.faq.exception.FAQNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(FAQNotFoundException::class)
    private fun handlePostNotFoundException(ex: FAQNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.name,
            message = ex.message ?: "세부 정보가 제공되지 않았습니다",
            path = request.getDescription(false)
        )

        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

}