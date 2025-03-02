package com.prewave.demo.web

import com.prewave.demo.core.exceptions.EdgeNotFoundException
import com.prewave.demo.core.exceptions.EdgeOperationNotAllowedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(EdgeOperationNotAllowedException::class)
    fun handleEdgeOperationNotAllowedException(
        ex: EdgeOperationNotAllowedException,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errorDetails = mapOf(
            "message" to ex.message,
            "status" to HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(EdgeNotFoundException::class)
    fun handleEdgeNotFoundException(
        ex: EdgeNotFoundException,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errorDetails = mapOf(
            "message" to ex.message,
            "status" to HttpStatus.NOT_FOUND.value()
        )
        return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Validation failed")
        }

        val errorDetails = mapOf(
            "message" to "Validation failed",
            "errors" to errors,
            "status" to HttpStatus.BAD_REQUEST.value()
        )

        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(
        ex: HttpRequestMethodNotSupportedException,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity("", HttpStatus.METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errorDetails = "An unexpected error occurred."
        logger.error(errorDetails, ex)
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
