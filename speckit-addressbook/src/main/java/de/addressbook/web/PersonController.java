package de.addressbook.web;

import java.net.URI;

import jakarta.validation.Valid;

import de.addressbook.model.Person;
import de.addressbook.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-Controller fuer den ID-basierten, programmatischen Zugriff auf Personen
 * (research.md Entscheidung 9, {@code contracts/openapi.yaml}).
 */
@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(final PersonService personService) {
        super();

        this.personService = personService;
    }

    /**
     * {@code POST /api/persons} - legt einen neuen Eintrag an (FR-001, FR-004).
     */
    @PostMapping
    public ResponseEntity<PersonResponse> createPerson(@Valid @RequestBody final PersonRequest request) {
        final Person created = personService.create(request);

        return ResponseEntity.created(URI.create("/api/persons/" + created.id()))
                .body(PersonResponse.from(created));
    }

    /**
     * {@code DELETE /api/persons/{id}} - loescht einen bestehenden Eintrag (FR-009, FR-012;
     * 204/404).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonById(@PathVariable final long id) {
        personService.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET /api/persons/{id}} - liefert einen Eintrag ueber seine ID (FR-010).
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> getPersonById(@PathVariable final long id) {
        return ResponseEntity.ok(PersonResponse.from(personService.getById(id)));
    }

    /**
     * {@code GET /api/persons?q=} - sucht Personen (case-insensitive Teilstring auf Vor-
     * oder Nachname) oder liefert bei fehlendem/leerem {@code q} alle Eintraege
     * (FR-005-007, FR-017).
     */
    @GetMapping
    public ResponseEntity<PersonPageResponse> search(
            @RequestParam(name = "q", required = false) final String q,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size
    ) {
        return ResponseEntity.ok(PersonPageResponse.from(personService.search(q, page, size)));
    }

    /**
     * {@code PUT /api/persons/{id}} - aendert Vorname/Nachname eines bestehenden Eintrags
     * unter Optimistic-Locking-Kontrolle (FR-011; 200/400/404/409).
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> updatePersonById(
            @PathVariable final long id, @Valid @RequestBody final PersonUpdateRequest request) {
        final Person updated = personService.update(id, request.toPersonRequest(), request.version());

        return ResponseEntity.ok(PersonResponse.from(updated));
    }
}
