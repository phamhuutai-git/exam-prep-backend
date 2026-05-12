package com.example.examprepbackend.exception;

import com.example.examprepbackend.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j // Thêm cái này để dùng log.error cho chuyên nghiệp
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

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // --- ĐÂY LÀ ĐOẠN QUAN TRỌNG NHẤT ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        // 1. In toàn bộ "dấu vết" lỗi ra IntelliJ để bạn debug
        ex.printStackTrace();

        // 2. Log lỗi bằng SLF4J (nếu cần xem trong log file)
        log.error("Hệ thống gặp lỗi nghiêm trọng: ", ex);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);

        // 3. Tạm thời trả về tin nhắn lỗi thật để bạn xem trên trình duyệt cho nhanh
        response.put("message", "Lỗi hệ thống: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}