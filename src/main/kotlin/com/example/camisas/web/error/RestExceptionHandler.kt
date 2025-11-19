package com.example.camisas.web.error

import com.example.camisas.service.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

data class ApiError(val status: Int, val error: String, val message: String?)

@ControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiError(404, "NOT_FOUND", ex.message))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiError(400, "BAD_REQUEST", ex.message))

    @ExceptionHandler(MethodArgumentNotValidException::class, HttpMessageNotReadableException::class)
    fun handleValidation(ex: Exception): ResponseEntity<ApiError> {
        val msg = when (ex) {
            is MethodArgumentNotValidException ->
                ex.bindingResult.fieldErrors.joinToString { "${it.field}: ${it.defaultMessage}" }
            is HttpMessageNotReadableException -> ex.mostSpecificCause?.message ?: ex.message
            else -> ex.message
        }
        return ResponseEntity.badRequest().body(ApiError(400, "VALIDATION_ERROR", msg))
    }
}