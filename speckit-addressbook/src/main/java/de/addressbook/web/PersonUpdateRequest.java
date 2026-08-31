package de.addressbook.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request-DTO fuer {@code PUT /api/persons/{id}} gemaess {@code contracts/openapi.yaml} ->
 * {@code PersonUpdateRequest} (erweitert {@link PersonRequest} um {@code version} fuer
 * Optimistic Locking, research.md Entscheidung 4).
 */
public record PersonUpdateRequest(
        @NotBlank(message = "darf nicht leer sein und muss 1 bis 100 Zeichen lang sein")
        @Size(min = 1, max = 100, message = "darf nicht leer sein und muss 1 bis 100 Zeichen lang sein")
        String firstName,

        @NotBlank(message = "darf nicht leer sein und muss 1 bis 100 Zeichen lang sein")
        @Size(min = 1, max = 100, message = "darf nicht leer sein und muss 1 bis 100 Zeichen lang sein")
        String lastName,

        long version) {

    /**
     * Wandelt in ein {@link PersonRequest} (ohne {@code version}) um, damit
     * {@code PersonService.update} dieselbe Trim-/Validierungslogik wie beim Anlegen
     * (research.md Entscheidung 6) wiederverwenden kann.
     */
    public PersonRequest toPersonRequest() {
        return new PersonRequest(firstName, lastName);
    }
}
