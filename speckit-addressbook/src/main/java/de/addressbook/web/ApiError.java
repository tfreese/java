package de.addressbook.web;

import java.util.List;

/**
 * Fehler-DTO gemaess {@code contracts/openapi.yaml} Schema {@code Error}.
 */
public record ApiError(String code, String message, List<FieldError> fieldErrors) {

    public static ApiError of(final String code, final String message) {
        return new ApiError(code, message, List.of());
    }

    public record FieldError(String field, String message) {
    }
}
