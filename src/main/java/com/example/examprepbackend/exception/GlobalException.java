package com.example.examprepbackend.exception;

import com.example.examprepbackend.common.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<BaseResponse> handleApplicationException(ApplicationException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(null, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException notValidException) {
        List<String> fullErrorMessages = new ArrayList<>();

        List<FieldError> fieldErrorList = notValidException.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrorList) {
            fullErrorMessages.add(fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(new BaseResponse<>(null, "Not valid: " + fullErrorMessages));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 403,
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 409,
                "message", ex.getMessage()
        ));



    }
}
