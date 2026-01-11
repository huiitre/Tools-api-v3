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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(
        RuntimeException ex,
        HttpServletRequest request
    ) {

        // Message API court (cause racine si dispo)
        Throwable root = ex;
        while (root.getCause() != null) root = root.getCause();

        List<String> trace = Arrays.stream(ex.getStackTrace())
            .map(StackTraceElement::toString)
            .limit(5)
            .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

        // Ajouter le vrai message d'erreur en dernier
        trace.add("CAUSE: " + root.getMessage());

        logger.error("ERROR:\n{}", String.join("\n", trace));

        boolean exposeTrace = env.acceptsProfiles("dev", "qa");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        if (exposeTrace) {
            body.put("trace", trace);
        }

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(body);
    }
}
