package com.example.urlshorter.exceptionhandler;

import com.example.urlshorter.dto.Violation;
import com.example.urlshorter.dto.Violations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * A global exception handler to handle exceptions for all controllers in this application.
 * Extends ResponseEntityExceptionHandler to reuse predefined spring MVC exception handling.
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * A generic handler for all errors and exceptions whose handling is not defined.
     * @param throwable Throwable
     * @return Violation
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Violation> handleAllUncaughtExceptions(Throwable throwable) {
        log.error(ExceptionUtils.getStackTrace(throwable));
        return ResponseEntity.internalServerError().body(new Violation("Please check logs for more details"));
    }
}
