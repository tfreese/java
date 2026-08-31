package de.addressbook.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request-DTO fuer Anlegen/Aendern (gemeinsame Felder). Traegt Bean-Validation-Annotationen
 * gemaess {@code contracts/openapi.yaml} -&gt; {@code PersonRequest} und
 * {@code data-model.md} (1-100 Zeichen nach Trimmen, FR-002, FR-003, FR-015).
 * <p>
 * Die Validierung wird von {@link de.addressbook.service.PersonService} programmatisch auf
 * den bereits getrimmten Werten ausgefuehrt (research.md Entscheidung 6), damit Controller
 * und JSF-Backing-Bean identisch behandelt werden.
 */
public record PersonRequest(
        @NotBlank(message = "darf nicht leer sein und muss 1 bis 100 Zeichen lang sein")
        @Size(min = 1, max = 100, message = "darf nicht leer sein und muss 1 bis 100 Zeichen lang sein")
        String firstName,

        @NotBlank(message = "darf nicht leer sein und muss 1 bis 100 Zeichen lang sein")
        @Size(min = 1, max = 100, message = "darf nicht leer sein und muss 1 bis 100 Zeichen lang sein")
        String lastName) {
}
