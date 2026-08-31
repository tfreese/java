package de.addressbook.service;

/**
 * Wird geworfen, wenn eine Aenderung mit einer veralteten {@code version} (Optimistic
 * Locking) auf einen zwischenzeitlich anderweitig geaenderten Eintrag angewendet wird
 * (research.md Entscheidung 4).
 */
public class OptimisticLockException extends RuntimeException {

    private final long id;

    public OptimisticLockException(final long id) {
        super("Der Eintrag mit ID " + id + " wurde zwischenzeitlich von anderer Stelle geaendert.");

        this.id = id;
    }

    public long getId() {
        return id;
    }
}
