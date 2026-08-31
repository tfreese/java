package de.addressbook.web;

import java.io.Serial;
import java.util.List;
import java.util.Map;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.validation.ConstraintViolationException;

import de.addressbook.model.Person;
import de.addressbook.service.OptimisticLockException;
import de.addressbook.service.PersonNotFoundException;
import de.addressbook.service.PersonPage;
import de.addressbook.service.PersonService;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * JSF-Backing-Bean fuer die PrimeFaces-Oberflaeche ({@code persons.xhtml}). Bildet den
 * API-Layer fuer die UI ab (analog zu {@link PersonController} fuer REST, research.md
 * Entscheidung 9) und delegiert Anlege-/Aenderungs-/Loesch-Aktionen an {@link PersonService}.
 * <p>
 * Grundgeruest (T023): Formularbindung fuer das Anlegen eines neuen Eintrags (Vorname,
 * Nachname, {@code save()}). Such-/Tabellen-/Bearbeitungs-/Loesch-Funktionalitaet folgen in
 * den Tasks der User Stories 2-4.
 */
@Component("personBean")
@Scope("session")
public class PersonBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonBean.class);

    /**
     * Mutable Sicht auf {@link Person} fuer die editierbare PrimeFaces-Tabelle (T040): Der
     * unveraenderliche Domaenen-Record {@link Person} besitzt keine Setter, die
     * PrimeFaces-Inline-Bearbeitung (Cell-Editor) benoetigt jedoch JavaBean-Setter fuer die
     * bearbeiteten Spalten.
     */
    public static final class PersonRow {

        private final long id;

        private String firstName;
        private String lastName;
        private long version;

        PersonRow(final Person person) {
            super();

            this.id = person.id();
            this.firstName = person.firstName();
            this.lastName = person.lastName();
            this.version = person.version();
        }

        public String getFirstName() {
            return firstName;
        }

        public long getId() {
            return id;
        }

        public String getLastName() {
            return lastName;
        }

        public long getVersion() {
            return version;
        }

        public void setFirstName(final String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(final String lastName) {
            this.lastName = lastName;
        }

        void applyUpdated(final Person person) {
            this.firstName = person.firstName();
            this.lastName = person.lastName();
            this.version = person.version();
        }
    }

    private final class PersonLazyDataModel extends LazyDataModel<PersonRow> {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public int count(final Map<String, FilterMeta> filterBy) {
            return (int) personService.search(query, 0, 1).totalElements();
        }

        @Override
        public List<PersonRow> load(final int loadFirst, final int pageSize, final Map<String, SortMeta> sortBy, final Map<String, FilterMeta> filterBy) {
            final int effectivePageSize = pageSize <= 0 ? PersonService.DEFAULT_PAGE_SIZE : pageSize;
            final int page = loadFirst / effectivePageSize;
            final PersonPage result = personService.search(query, page, effectivePageSize);

            setRowCount((int) result.totalElements());

            return result.content().stream().map(PersonRow::new).toList();
        }
    }

    private final PersonService personService;
    private final LazyDataModel<PersonRow> persons;

    private String errorMessage;
    private String firstName;
    private String lastName;
    private String query;
    private String rowErrorMessage;

    public PersonBean(final PersonService personService) {
        super();

        this.personService = personService;
        this.persons = new PersonLazyDataModel();
    }

    /**
     * Loescht die uebergebene Zeile ueber {@link PersonService#delete(long)} und aktualisiert
     * die angezeigte Ergebnisliste (FR-009, FR-012). Existiert der Eintrag nicht mehr (z. B.
     * bereits anderweitig geloescht), wird eine entsprechende Meldung angezeigt und die
     * Ergebnisliste dennoch aktualisiert.
     */
    public void delete(final PersonRow row) {
        rowErrorMessage = null;

        try {
            personService.delete(row.getId());
            LOGGER.info("Person {} ueber JSF-Tabelle geloescht", row.getId());

            final int currentCount = persons.getRowCount();

            if (currentCount > 0) {
                persons.setRowCount(currentCount - 1);
            }
        }
        catch (final PersonNotFoundException _) {
            rowErrorMessage = "Der Eintrag existiert nicht mehr.";
        }
        // finally {
        //     persons.u;
        // }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LazyDataModel<PersonRow> getPersons() {
        return persons;
    }

    public String getQuery() {
        return query;
    }

    public String getRowErrorMessage() {
        return rowErrorMessage;
    }

    /**
     * PrimeFaces-Callback beim Abbrechen der Zeilenbearbeitung; es sind keine weiteren
     * Aktionen erforderlich, da {@link PersonRow} erst bei erfolgreichem
     * {@link #onRowEdit(RowEditEvent)} aktualisiert wird.
     */
    public void onRowCancel(final RowEditEvent<PersonRow> event) {
        rowErrorMessage = null;

        final PersonRow row = event.getObject();

        final FacesMessage msg = new FacesMessage("Edit Cancelled", row.getLastName() + ", " + row.getFirstName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * PrimeFaces-Cell-Editor-Callback fuer eine bearbeitete Tabellenzeile (FR-008, FR-011):
     * delegiert an {@link PersonService#update(long, PersonRequest, long)} und zeigt bei
     * einem Optimistic-Locking-Konflikt (HTTP 409-Aequivalent) eine Konfliktmeldung
     * ({@link #rowErrorMessage}) an, statt die Aenderung stillschweigend zu verwerfen.
     */
    public void onRowEdit(final RowEditEvent<PersonRow> event) {
        rowErrorMessage = null;

        final PersonRow row = event.getObject();

        try {
            final Person updated = personService.update(row.getId(), new PersonRequest(row.getFirstName(), row.getLastName()), row.getVersion());

            row.applyUpdated(updated);

            LOGGER.info("Person {} ueber JSF-Tabelle geaendert", updated.id());

            final FacesMessage msg = new FacesMessage("Person Edited", row.getLastName() + ", " + row.getFirstName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        catch (final OptimisticLockException _) {
            rowErrorMessage = "Der Eintrag wurde zwischenzeitlich von anderer Stelle geaendert. Bitte die Ergebnisliste neu laden.";
        }
        catch (final PersonNotFoundException _) {
            rowErrorMessage = "Der Eintrag existiert nicht mehr.";
        }
        catch (final ConstraintViolationException _) {
            rowErrorMessage = "Vorname und Nachname duerfen nicht leer sein und muessen 1 bis 100 Zeichen lang sein.";
        }
    }

    public void onRowSelect(final SelectEvent<PersonRow> event) {
        final PersonRow row = event.getObject();

        final FacesMessage msg = new FacesMessage("Customer Selected", row.getLastName() + ", " + row.getFirstName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Legt einen neuen Eintrag mit den aktuellen Formularwerten an (FR-001-003) und leert
     * anschliessend das Formular. Validierungsfehler werden als {@link #errorMessage}
     * fuer die Anzeige in der View bereitgestellt.
     */
    public void save() {
        errorMessage = null;

        try {
            final Person created = personService.create(new PersonRequest(firstName, lastName));
            LOGGER.info("Person {} ueber JSF-Formular angelegt", created.id());

            firstName = null;
            lastName = null;
        }
        catch (final ConstraintViolationException _) {
            errorMessage = "Vorname und Nachname duerfen nicht leer sein und muessen 1 bis 100 Zeichen lang sein.";
        }
    }

    public void search() {
        // Empty
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public void showAll() {
        query = null;
    }
}
