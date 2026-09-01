package de.addressbook.service;

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import de.addressbook.model.Person;
import de.addressbook.repository.PersonRepository;
import de.addressbook.web.PersonRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Fachlogik fuer Personen: Trimmen, Validierung, Sortierung, Suche (siehe plan.md).
 * <p>
 * Logging-Konvention (Constitution Principle VIII, research.md Entscheidung 7): Alle
 * kuenftigen Methoden dieser Klasse MUESSEN auf Level {@code INFO} ausschliesslich die
 * Personen-ID referenzieren; Vor-/Nachname duerfen hoechstens auf Level {@code DEBUG}
 * geloggt werden.
 */
@Service
public class PersonService {

    public static final int DEFAULT_PAGE_SIZE = 20;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MIN_LENGTH = 1;
    private final PersonRepository personRepository;

    private final Validator validator;

    public PersonService(final PersonRepository personRepository, final Validator validator) {
        super();

        this.personRepository = personRepository;
        this.validator = validator;
    }

    /**
     * Legt einen neuen Eintrag an (FR-001-004, FR-014-016): Trimmen (FR-015), anschliessend
     * Bean-Validation auf den getrimmten Werten (research.md Entscheidung 6), dann Aufruf
     * von {@link PersonRepository#insert(Person)}. Es gibt keine Eindeutigkeitspruefung auf
     * die Namenskombination (FR-016) - mehrere Personen mit identischem Namen sind zulaessig.
     *
     * @throws ConstraintViolationException wenn die getrimmten Werte die Validierungsregeln
     * verletzen (mappt auf HTTP 400, FR-014)
     */
    public Person create(final PersonRequest request) {
        final PersonRequest trimmed = new PersonRequest(trim(request.firstName()), trim(request.lastName()));
        validate(trimmed);

        return personRepository.insert(Person.newEntry(trimmed.firstName(), trimmed.lastName()));
    }

    /**
     * Loescht einen bestehenden Eintrag ueber seine ID (FR-009, FR-012).
     *
     * @throws PersonNotFoundException wenn keine Person mit der angegebenen ID existiert
     * (FR-013, mappt auf HTTP 404)
     */
    public void delete(final long id) {
        final boolean deleted = personRepository.deleteById(id);
        if (!deleted) {
            throw new PersonNotFoundException(id);
        }
    }

    /**
     * Liefert einen Eintrag ueber seine ID (FR-010).
     *
     * @throws PersonNotFoundException wenn keine Person mit der angegebenen ID existiert
     * (FR-013, mappt auf HTTP 404)
     */
    public Person getById(final long id) {
        final Person person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));

        LOGGER.debug("Person {} abgerufen", id);

        return person;
    }

    public PersonPage search(final String query, final Integer page, final Integer size) {
        final int safePage = page == null ? 0 : page;
        final int safeSize = size == null ? DEFAULT_PAGE_SIZE : size;

        if (safePage < 0) {
            throw new IllegalArgumentException("page darf nicht negativ sein");
        }

        if (safeSize < MIN_LENGTH || safeSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("size muss zwischen 1 und " + MAX_PAGE_SIZE + " liegen");
        }

        final int limit = safeSize;
        final int offset = safePage * safeSize;

        final String trimmedQuery = query == null ? null : query.trim();
        final List<Person> content = personRepository.search(trimmedQuery, limit, offset);
        final long totalElements = personRepository.count(trimmedQuery);
        final int totalPages = (int) Math.ceil((double) totalElements / safeSize);

        return new PersonPage(content, safePage, safeSize, totalElements, totalPages);
    }

    /**
     * Aendert Vorname und/oder Nachname eines bestehenden Eintrags (FR-011): Trimmen (FR-015),
     * Bean-Validation auf den getrimmten Werten (research.md Entscheidung 6), Existenzpruefung
     * (FR-013), anschliessend versionsgeschuetztes Update (Optimistic Locking, research.md
     * Entscheidung 4).
     *
     * @param id ID des zu aendernden Eintrags
     * @param request neue Werte fuer Vorname/Nachname
     * @param version zuletzt gelesener Optimistic-Locking-Wert
     *
     * @throws PersonNotFoundException wenn keine Person mit der ID existiert (FR-013,
     * mappt auf HTTP 404)
     * @throws ConstraintViolationException wenn die getrimmten Werte die Validierungsregeln
     * verletzen (mappt auf HTTP 400, FR-014)
     * @throws OptimisticLockException wenn {@code version} nicht mehr dem aktuellen
     * DB-Stand entspricht (mappt auf HTTP 409)
     */
    public Person update(final long id, final PersonRequest request, final long version) {
        final PersonRequest trimmed = new PersonRequest(trim(request.firstName()), trim(request.lastName()));
        validate(trimmed);

        personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));

        final int rowsAffected = personRepository.update(id, trimmed.firstName(), trimmed.lastName(), version);

        if (rowsAffected == 0) {
            throw new OptimisticLockException(id);
        }

        return personRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Person mit ID " + id + " konnte nach dem Update nicht gefunden werden."));
    }

    /**
     * Trimmt fuehrende/abschliessende Leerzeichen (FR-015); {@code null} bleibt {@code null}.
     * MUSS vor jeder weiteren Validierung/Verarbeitung von Vorname/Nachname aufgerufen werden,
     * konsistent fuer Anlegen und Aendern.
     */
    String trim(final String value) {
        return value == null ? null : value.trim();
    }

    private void validate(final PersonRequest request) {
        final Set<ConstraintViolation<PersonRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
