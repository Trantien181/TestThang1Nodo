package org.example.testthang1nodo.Exception;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

//@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", ex.getErrorCode().getCode());

        String message = messageSource.getMessage(
                ex.getErrorCode().getMessage(),
                null,
                ex.getMessage(),
                LocaleContextHolder.getLocale()
        );
        errorResponse.put("message", message);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", ErrorCode.INVALID_REQUEST.getCode());

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> {
                            // Kiểm tra nếu lỗi là typeMismatch (lỗi định dạng sai)
                            if ("typeMismatch".equals(fieldError.getCode())) {
                                String fieldName = fieldError.getField();
                                String messageKey = "invalid.format." + fieldName;
                                return messageSource.getMessage(
                                        messageKey,
                                        new Object[]{fieldName, fieldError.getRejectedValue()},
                                        "Invalid format for field " + fieldName + ": " + fieldError.getRejectedValue(),
                                        LocaleContextHolder.getLocale()
                                );
                            }
                            // Xử lý các lỗi validate khác (như @NotBlank, @Size, v.v.)
                            return messageSource.getMessage(
                                    fieldError.getDefaultMessage(),
                                    null,
                                    fieldError.getDefaultMessage(),
                                    LocaleContextHolder.getLocale()
                            );
                        },
                        (existing, replacement) -> existing
                ));

        errorResponse.put("errors", fieldErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", ErrorCode.INVALID_FORMAT.getCode());

        Map<String, String> fieldErrors = new HashMap<>();

        String fieldName = ex.getName(); // Tên tham số bị lỗi
        Object rejectedValue = ex.getValue(); // Giá trị người dùng nhập sai

        // Key message dạng: invalid.format.{field}
        String messageKey = "invalid.format." + fieldName;
        String defaultMessage = "Invalid format for field " + fieldName + ": " + rejectedValue;

        String resolvedMessage = messageSource.getMessage(
                messageKey,
                new Object[]{fieldName, rejectedValue},
                defaultMessage,
                LocaleContextHolder.getLocale()
        );

        fieldErrors.put(fieldName, resolvedMessage);
        errorResponse.put("errors", fieldErrors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseException(DataIntegrityViolationException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", ErrorCode.DATABASE_ERROR.getCode());

        String message = messageSource.getMessage(
                ErrorCode.DATABASE_ERROR.getMessage(),
                null,
                "Database error occurred",
                LocaleContextHolder.getLocale()
        );
        errorResponse.put("message", message);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", "INTERNAL_SERVER_ERROR");
        errorResponse.put("message", messageSource.getMessage(
                "internal.server.error",
                null,
                "Internal server error",
                LocaleContextHolder.getLocale()
        ));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}