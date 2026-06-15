package com.alora.exception;

import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private Map<String, Object> body(int status, String error, String msg, WebRequest req) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", status,
                "error", error,
                "message", msg,
                "path", req.getDescription(false).replace("uri=","")
        );
    }



    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(404, "Not Found", ex.getMessage(), req));
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPin(InvalidPinException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body(400, "Bad Request", ex.getMessage(), req));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body(400, "Bad Request", ex.getMessage(), req));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest req) {
        Map<String, String> validationErrors = new HashMap<>();

        // Extraemos qué campos han fallado y su mensaje
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        }

        // Creamos la respuesta base
        Map<String, Object> response = new HashMap<>(body(400, "Validation Error", "Los datos enviados no son válidos", req));
        // Le añadimos la lista detallada de errores
        response.put("validationErrors", validationErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 2. Captura credenciales incorrectas en el Login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(body(401, "Unauthorized", "Email o contraseña incorrectos", req));
    }
}
