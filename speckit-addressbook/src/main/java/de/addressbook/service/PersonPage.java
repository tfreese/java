package de.addressbook.service;

import de.addressbook.model.Person;

import java.util.List;

/**
 * Business-Layer-Ergebnis einer paginierten Suche (FR-005-FR-008, FR-016).
 * Wird im API-Layer auf {@code PersonPageResponse} abgebildet.
 */
public record PersonPage(List<Person> content, int page, int size, long totalElements, int totalPages) {
}
