package de.addressbook.model;

import java.time.LocalDateTime;

/**
 * Domaenenmodell eines Adressbucheintrags (Persistence + Business).
 * Entspricht der Tabelle {@code PERSON} (siehe data-model.md).
 */
public record Person(
        Long id,
        String firstName,
        String lastName,
        LocalDateTime createdAt,
        long version) {

    /**
     * Erstellt eine noch nicht persistierte Person (id/createdAt unbekannt, version = 0).
     * Dient als Uebergabeobjekt an {@code PersonRepository.insert(Person)}.
     */
    public static Person newEntry(final String firstName, final String lastName) {
        return new Person(null, firstName, lastName, null, 0L);
    }
}
