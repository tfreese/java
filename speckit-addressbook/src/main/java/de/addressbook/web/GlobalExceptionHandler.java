package de.addressbook.web;

import java.util.List;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

import de.addressbook.service.OptimisticLockException;
import de.addressbook.service.PersonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Uebersetzt fachliche Exceptions in HTTP-Antworten gemaess {@code contracts/openapi.yaml}
 * (FR-013: "nicht gefunden" -&gt; 404, Optimistic-Locking-Konflikt -&gt; 409,
 * FR-014: Validierungsfehler -&gt; 400).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static String lastNode(final Path path) {
        String last = null;

        for (final Path.Node node : path) {
            last = node.getName();
        }

        return last;
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiError> handleConflict(final OptimisticLockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("CONFLICT", ex.getMessage()));
    }

    /**
     * Faengt programmatische Bean-Validation-Verstoesse aus {@code PersonService} ab
     * (getrimmte Werte, research.md Entscheidung 6) und mappt sie ebenfalls auf 400.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(final ConstraintViolationException ex) {
        final List<ApiError.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(cv -> new ApiError.FieldError(lastNode(cv.getPropertyPath()), cv.getMessage()))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("VALIDATION_ERROR", "Ungueltige Eingabe.", fieldErrors));
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(final PersonNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(final MethodArgumentNotValidException ex) {
        final List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("VALIDATION_ERROR", "Ungueltige Eingabe.", fieldErrors));
    }
}
