package de.addressbook.service;

/**
 * Wird geworfen, wenn ein Eintrag ueber eine unbekannte ID abgerufen, geaendert oder
 * geloescht werden soll (FR-013).
 */
public class PersonNotFoundException extends RuntimeException {

    private final long id;

    public PersonNotFoundException(final long id) {
        super("Person mit ID " + id + " nicht gefunden.");

        this.id = id;
    }

    public long getId() {
        return id;
    }
}
