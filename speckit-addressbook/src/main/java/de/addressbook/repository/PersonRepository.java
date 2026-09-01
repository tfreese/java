package de.addressbook.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import de.addressbook.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * JDBC-DAO fuer den Zugriff auf die Tabelle {@code PERSON} (embedded H2).
 * Ausschliesslich JDBC ueber {@link JdbcTemplate}, kein ORM (Constitution Principle V,
 * tech-stack.md: "Persistenz: JDBC, Zugriff ausschliesslich ueber DAOs").
 */
@Repository
public class PersonRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonRepository.class);
    private static final RowMapper<Person> ROW_MAPPER = PersonRepository::mapRow;
    private static final String SEARCH_WHERE = " WHERE LOWER(FIRSTNAME) LIKE LOWER(?) OR LOWER(LASTNAME) LIKE LOWER(?)";
    private static final String SELECT_COLUMNS = "SELECT ID, FIRSTNAME, LASTNAME, CREATED_AT, VERSION FROM PERSON";

    private static Person mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        return new Person(
                rs.getLong("ID"),
                rs.getString("FIRSTNAME"),
                rs.getString("LASTNAME"),
                rs.getTimestamp("CREATED_AT").toLocalDateTime(),
                rs.getLong("VERSION"));
    }

    private final JdbcTemplate jdbcTemplate;

    public PersonRepository(final JdbcTemplate jdbcTemplate) {
        super();

        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Liefert die Gesamtanzahl der zu {@code query} passenden Eintraege fuer die
     * Pagination-Metadaten (FR-016); dieselbe Filterlogik wie {@link #search}.
     */
    public long count(final String query) {
        final String pattern = "%" + (query == null ? "" : query.toLowerCase()) + "%";
        final Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM PERSON" + SEARCH_WHERE, Long.class, pattern, pattern);

        return total == null ? 0L : total;
    }

    /**
     * Loescht einen Eintrag ueber seine ID (FR-009, FR-012).
     *
     * @return {@code true}, wenn ein Eintrag mit der angegebenen ID vorhanden war und
     * geloescht wurde; {@code false}, wenn keiner existierte
     */
    public boolean deleteById(final long id) {
        final int rowsAffected = jdbcTemplate.update("DELETE FROM PERSON WHERE ID = ?", id);

        if (rowsAffected > 0) {
            LOGGER.info("Person {} geloescht", id);
        }

        return rowsAffected > 0;
    }

    /**
     * Liefert einen Eintrag ueber seine ID, oder {@link Optional#empty()}, wenn keiner
     * existiert (FR-010, FR-013). Enthaelt die {@code version}-Spalte fuer Optimistic Locking.
     */
    public Optional<Person> findById(final long id) {
        return jdbcTemplate.query(SELECT_COLUMNS + " WHERE ID = ?", ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    /**
     * Legt einen neuen Eintrag an. Die ID wird vorab explizit aus der Sequenz
     * {@code PERSON_SEQ} ermittelt (research.md Entscheidung 3, FR-004).
     *
     * @param person Uebergabeobjekt mit {@code firstName}/{@code lastName}
     * (z. B. via {@link Person#newEntry(String, String)}); {@code id},
     * {@code createdAt} und {@code version} werden ignoriert und
     * systemseitig vergeben.
     *
     * @return die persistierte Person inkl. vergebener ID, {@code createdAt} und {@code version}
     */
    public Person insert(final Person person) {
        final Long id = jdbcTemplate.queryForObject("SELECT NEXT VALUE FOR PERSON_SEQ", Long.class);

        jdbcTemplate.update(
                "INSERT INTO PERSON (ID, FIRSTNAME, LASTNAME) VALUES (?, ?, ?)",
                id, person.firstName(), person.lastName());

        LOGGER.debug("Person angelegt: id?{}, firstName={}, lastName={}", id, person.firstName(), person.lastName());

        return findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Person mit ID " + id + " konnte nach dem Anlegen nicht gefunden werden."));
    }

    /**
     * Case-insensitive Teilstring-Suche ueber Vor- UND Nachname, sortiert nach Nachname,
     * dann Vorname (research.md Entscheidung 2, FR-007, FR-017). Ein leerer oder
     * {@code null} Suchbegriff liefert alle Eintraege (begrenzt), da das Muster {@code %%}
     * auf jeden Wert passt (FR-006).
     * <p>
     * {@code limit}/{@code offset} begrenzen die Ergebnismenge serverseitig
     * (research.md Entscheidung 5), um ungefilterte Volltabellenscans zu vermeiden.
     *
     * @param query Suchbegriff (bereits getrimmt); {@code null}/leer bedeutet "alle"
     * @param limit maximale Anzahl Zeilen
     * @param offset Anzahl zu ueberspringender Zeilen
     */
    public List<Person> search(final String query, final int limit, final int offset) {
        final String pattern = "%" + (query == null ? "" : query.toLowerCase()) + "%";

        return jdbcTemplate.query(SELECT_COLUMNS
                        + " WHERE LOWER(FIRSTNAME) LIKE ? OR LOWER(LASTNAME) LIKE ? "
                        + "ORDER BY LASTNAME, FIRSTNAME "
                        + "LIMIT ? OFFSET ?",
                ROW_MAPPER, pattern, pattern, limit, offset);
        //
        // return jdbcTemplate.query(SELECT_COLUMNS
        //                 + " WHERE LOWER(FIRSTNAME) LIKE ? OR LOWER(LASTNAME) LIKE ? "
        //                 + "ORDER BY LASTNAME, FIRSTNAME "
        //         ,
        //         ROW_MAPPER, pattern, pattern);
    }

    /**
     * Aendert Vorname/Nachname eines bestehenden Eintrags unter Optimistic-Locking-Kontrolle.
     */
    public int update(final long id, final String firstName, final String lastName, final long expectedVersion) {
        final String sql = """
                UPDATE
                    PERSON
                SET
                    FIRSTNAME = ?,
                    LASTNAME = ?,
                    VERSION = VERSION + 1
                WHERE
                    ID = ?
                    AND VERSION = ?
                """;

        final int rowsAffected = jdbcTemplate.update(sql, firstName, lastName, id, expectedVersion);

        if (rowsAffected > 0) {
            LOGGER.debug("Person aktualisiert: id={},firstName={}, lastName={}", id, firstName, lastName);
        }

        return rowsAffected;
    }
}
