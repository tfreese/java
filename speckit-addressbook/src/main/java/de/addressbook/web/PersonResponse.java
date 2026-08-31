package de.addressbook.web;

import java.time.LocalDateTime;

import de.addressbook.model.Person;

/**
 * Response-DTO gemaess {@code contracts/openapi.yaml} -&gt; {@code PersonResponse}.
 */
public record PersonResponse(Long id, String firstName, String lastName, LocalDateTime createdAt, long version) {

    public static PersonResponse from(final Person person) {
        return new PersonResponse(person.id(), person.firstName(), person.lastName(), person.createdAt(), person.version());
    }
}
