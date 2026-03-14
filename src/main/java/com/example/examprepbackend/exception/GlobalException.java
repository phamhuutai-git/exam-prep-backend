package com.example.examprepbackend.exception;

import com.example.examprepbackend.common.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

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

}
