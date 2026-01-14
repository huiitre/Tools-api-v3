package fr.huiitre.tools.api.common;

import io.swagger.v3.oas.annotations.Hidden;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.core.env.Environment;

import fr.huiitre.tools.application.common.security.exception.ForbiddenException;
import fr.huiitre.tools.application.core.auth.exception.InvalidCredentialsException;
import fr.huiitre.tools.application.core.auth.exception.InvalidEmailVerificationTokenException;
import fr.huiitre.tools.application.core.auth.exception.UserDisabledException;
import fr.huiitre.tools.application.core.auth.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Hidden
@RestControllerAdvice
public class ApiSecurityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiSecurityExceptionHandler.class);
    private final Environment env;

    public ApiSecurityExceptionHandler(Environment env) {
        this.env = env;
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(
        ForbiddenException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.FORBIDDEN.value(),
                "error", "Forbidden",
                "message", "Access forbidden",
                "path", request.getRequestURI()
            ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
        IllegalArgumentException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", ex.getMessage(),
                "path", request.getRequestURI()
            ));
    }
}
