package org.juwont.web.advice;

import org.juwont.service.exception.ProductNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(final RuntimeException e) {
        return ResponseEntity.status(NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(final DataIntegrityViolationException e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse("Request contains an existing entity"));
    }

    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEntryException(final SQLIntegrityConstraintViolationException e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }


    @ExceptionHandler(value = InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEntryException(final InvalidDataAccessApiUsageException e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleFieldValidationError(final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getBindingResult().getFieldError());

        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse("Error on field (%s) : %s".formatted(fieldError.getField(), fieldError.getDefaultMessage())));
    }

    public record ErrorResponse(String errorMessage) {}
}
