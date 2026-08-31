package de.addressbook.repository;

import de.addressbook.model.Person;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Integrationstests fuer {@link PersonRepository} gegen embedded H2 (reale SQL-Pfade,
 * inkl. Sequenz und spaeter Optimistic-Locking-Konflikt).
 * Constitution Principle VII: Test Coverage Discipline (Persistence-Layer).
 */
@SpringBootTest
@Import(PersonRepository.class)
class PersonRepositoryTest {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private PersonRepository personRepository;

    @Test
    void contextLoads() {
        assertThat(personRepository).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
    }

    @Test
    void insertAssignsIdFromSequenceAndDefaultsCreatedAtAndVersion() {
        final Person inserted = personRepository.insert(Person.newEntry("Max", "Mustermann"));

        assertThat(inserted.id()).isNotNull();
        assertThat(inserted.firstName()).isEqualTo("Max");
        assertThat(inserted.lastName()).isEqualTo("Mustermann");
        assertThat(inserted.createdAt()).isNotNull();
        assertThat(inserted.version()).isZero();
    }

    @Test
    void insertAssignsDistinctIncreasingIdsFromPersonSeq() {
        final Person first = personRepository.insert(Person.newEntry("Anna", "Erste"));
        final Person second = personRepository.insert(Person.newEntry("Bea", "Zweite"));

        assertThat(second.id()).isGreaterThan(first.id());
    }

    @Test
    void findByIdReturnsPersistedEntryById() {
        final Person inserted = personRepository.insert(Person.newEntry("Clara", "Dritte"));

        final Optional<Person> found = personRepository.findById(inserted.id());

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(inserted.id());
        assertThat(found.get().firstName()).isEqualTo("Clara");
        assertThat(found.get().lastName()).isEqualTo("Dritte");
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        assertThat(personRepository.findById(999_999L)).isEmpty();
    }

    @Test
    void searchFindsCaseInsensitiveSubstringMatchesInFirstOrLastName() {
        final String token = "SrchTok" + UUID.randomUUID().toString().replace("-", "");

        personRepository.insert(Person.newEntry(token + "-Vorname", "Nachname"));
        personRepository.insert(Person.newEntry("Vorname", token + "-Nachname"));
        personRepository.insert(Person.newEntry("Anderer", "Name"));

        final List<Person> results = personRepository.search(token.toLowerCase(), 500, 0);

        assertThat(results).hasSize(2);
    }

    @Test
    void searchOrdersResultsByLastNameThenFirstName() {
        final String token = "SortTok" + UUID.randomUUID().toString().replace("-", "");

        personRepository.insert(Person.newEntry("Bea", token + "-Zeta"));
        personRepository.insert(Person.newEntry("Anna", token + "-Alpha"));
        personRepository.insert(Person.newEntry("Zoe", token + "-Alpha"));

        final List<Person> results = personRepository.search(token.toLowerCase(), 500, 0);

        assertThat(results)
                .extracting(Person::lastName, Person::firstName)
                .containsExactly(
                        tuple(token + "-Alpha", "Anna"),
                        tuple(token + "-Alpha", "Zoe"),
                        tuple(token + "-Zeta", "Bea"));
    }

    @Test
    void searchWithEmptyQueryReturnsAllEntries() {
        personRepository.insert(Person.newEntry("Egal", "Wurscht" + UUID.randomUUID()));

        final List<Person> results = personRepository.search("", 500, 0);

        assertThat(results).isNotEmpty();
    }

    @Test
    void searchReturnsEmptyListForNoMatches() {
        final String neverUsedToken = "NoMatchTok" + UUID.randomUUID();

        assertThat(personRepository.search(neverUsedToken, 500, 0)).isEmpty();
    }

    @Test
    void updateChangesFirstNameAndLastNameAndIncrementsVersion() {
        final Person inserted = personRepository.insert(Person.newEntry("Vor", "Nach"));

        final int rowsAffected = personRepository.update(
                inserted.id(), "NeuVor", "NeuNach", inserted.version());

        assertThat(rowsAffected).isEqualTo(1);

        final Person updated = personRepository.findById(inserted.id()).orElseThrow();
        assertThat(updated.firstName()).isEqualTo("NeuVor");
        assertThat(updated.lastName()).isEqualTo("NeuNach");
        assertThat(updated.version()).isEqualTo(inserted.version() + 1);
    }

    @Test
    void updateWithStaleVersionReturnsZeroRowsAffectedAndLeavesDataUnchanged() {
        final Person inserted = personRepository.insert(Person.newEntry("Vor", "Nach"));
        personRepository.update(inserted.id(), "ErsteAenderung", "Nach", inserted.version());

        final int rowsAffected = personRepository.update(
                inserted.id(), "ZweiteAenderung", "Nach", inserted.version());

        assertThat(rowsAffected).isZero();

        final Person unchanged = personRepository.findById(inserted.id()).orElseThrow();
        assertThat(unchanged.firstName()).isEqualTo("ErsteAenderung");
        assertThat(unchanged.version()).isEqualTo(inserted.version() + 1);
    }

    @Test
    void updateWithUnknownIdReturnsZeroRowsAffected() {
        assertThat(personRepository.update(999_999L, "Vor", "Nach", 0L)).isZero();
    }

    @Test
    void deleteByIdRemovesExistingEntryAndReturnsTrue() {
        final Person inserted = personRepository.insert(Person.newEntry("Vor", "Nach"));

        assertThat(personRepository.deleteById(inserted.id())).isTrue();
        assertThat(personRepository.findById(inserted.id())).isEmpty();
    }

    @Test
    void deleteByIdWithUnknownIdReturnsFalse() {
        assertThat(personRepository.deleteById(999_999L)).isFalse();
    }
}
