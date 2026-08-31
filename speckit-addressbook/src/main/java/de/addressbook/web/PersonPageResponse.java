package de.addressbook.web;

import java.util.List;

import de.addressbook.service.PersonPage;

/**
 * Ausgabe-DTO fuer paginierte Ergebnislisten ({@code GET /api/persons}, FR-016).
 */
public record PersonPageResponse(List<PersonResponse> content, int page, int size, long totalElements, int totalPages) {

    public static PersonPageResponse from(final PersonPage page) {
        final List<PersonResponse> content = page.content().stream()
                .map(PersonResponse::from)
                .toList();

        return new PersonPageResponse(content, page.page(), page.size(), page.totalElements(), page.totalPages());
    }
}
