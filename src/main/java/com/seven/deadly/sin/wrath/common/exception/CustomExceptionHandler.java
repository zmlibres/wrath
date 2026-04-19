package com.seven.deadly.sin.wrath.common.exception;

import com.seven.deadly.sin.wrath.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ResourceExistException.class)
    public ResponseEntity<ErrorResponse> handleResourceExistException(ResourceExistException e) {
        return buildException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return buildException(e, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorResponse> buildException(RuntimeException e, HttpStatus status) {

        return ResponseEntity.status(status)
                             .body(ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .status(status.value())
                                                .error(status.getReasonPhrase())
                                                .message(e.getMessage())
                                                .build());


    }
}
