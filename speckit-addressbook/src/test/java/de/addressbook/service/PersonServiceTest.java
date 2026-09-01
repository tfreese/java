package de.addressbook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import de.addressbook.model.Person;
import de.addressbook.repository.PersonRepository;
import de.addressbook.web.PersonRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;
    private PersonService personService;
    private Validator validator;

    @Test
    void createAllowsDuplicateNamesSinceOnlyIdIsUnique() {
        when(personRepository.insert(any(Person.class)))
                .thenAnswer(invocation -> new Person(1L, "Max", "Mustermann", null, 0L))
                .thenAnswer(invocation -> new Person(2L, "Max", "Mustermann", null, 0L));

        final Person first = personService.create(new PersonRequest("Max", "Mustermann"));
        final Person second = personService.create(new PersonRequest("Max", "Mustermann"));

        assertThat(first.id()).isNotEqualTo(second.id());
    }

    @Test
    void createRejectsBlankFirstNameAfterTrim() {
        assertThatThrownBy(() -> personService.create(new PersonRequest("   ", "Mustermann")))
                .isInstanceOf(ConstraintViolationException.class);

        verifyNoInteractions(personRepository);
    }

    @Test
    void createRejectsFirstNameLongerThan100CharsAfterTrim() {
        final String tooLong = "A".repeat(101);

        assertThatThrownBy(() -> personService.create(new PersonRequest(tooLong, "Mustermann")))
                .isInstanceOf(ConstraintViolationException.class);

        verifyNoInteractions(personRepository);
    }

    @Test
    void createTrimsFirstNameAndLastNameBeforeInsert() {
        when(personRepository.insert(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        personService.create(new PersonRequest("  Max  ", "  Mustermann  "));

        verify(personRepository).insert(
                new Person(null, "Max", "Mustermann", null, 0L));
    }

    @Test
    void deleteRemovesEntryWhenPresent() {
        when(personRepository.deleteById(1L)).thenReturn(true);

        personService.delete(1L);

        verify(personRepository).deleteById(1L);
    }

    @Test
    void deleteThenGetByIdThrowsPersonNotFoundException() {
        when(personRepository.deleteById(1L)).thenReturn(true);
        when(personRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        personService.delete(1L);

        assertThatThrownBy(() -> personService.getById(1L))
                .isInstanceOf(PersonNotFoundException.class);
    }

    @Test
    void deleteThrowsPersonNotFoundExceptionWhenMissing() {
        when(personRepository.deleteById(999L)).thenReturn(false);

        assertThatThrownBy(() -> personService.delete(999L))
                .isInstanceOf(PersonNotFoundException.class);
    }

    @Test
    void getByIdReturnsPersonWhenFound() {
        final Person person = new Person(1L, "Max", "Mustermann", null, 0L);
        when(personRepository.findById(1L)).thenReturn(java.util.Optional.of(person));

        assertThat(personService.getById(1L)).isEqualTo(person);
    }

    @Test
    void getByIdThrowsPersonNotFoundExceptionWhenMissing() {
        when(personRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> personService.getById(999L))
                .isInstanceOf(PersonNotFoundException.class);
    }

    @Test
    void searchTrimsQueryAndDelegatesToRepositoryWithDefaultLimit() {
        final List<Person> expected = List.of(new Person(1L, "Max", "Mustermann", null, 0L));
        when(personRepository.search("mustermann", PersonService.DEFAULT_PAGE_SIZE, 0))
                .thenReturn(expected);

        final PersonPage result = personService.search("  mustermann  ", 0, PersonService.DEFAULT_PAGE_SIZE);

        assertThat(result.content()).isEqualTo(expected);
        verify(personRepository).search("mustermann", PersonService.DEFAULT_PAGE_SIZE, 0);
    }

    @Test
    void searchWithBlankQueryDelegatesWithEmptyStringForAllEntries() {
        personService.search("   ", 0, PersonService.DEFAULT_PAGE_SIZE);

        verify(personRepository).search("", PersonService.DEFAULT_PAGE_SIZE, 0);
    }

    @Test
    void searchWithNullQueryDelegatesWithEmptyStringForAllEntries() {
        personService.search(null, 0, PersonService.DEFAULT_PAGE_SIZE);

        verify(personRepository).search(null, PersonService.DEFAULT_PAGE_SIZE, 0);
    }

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        personService = new PersonService(personRepository, validator);
    }

    @Test
    void trimRemovesLeadingAndTrailingWhitespace() {
        assertThat(personService.trim("  Max  ")).isEqualTo("Max");
    }

    @Test
    void trimReturnsNullForNullInput() {
        assertThat(personService.trim(null)).isNull();
    }

    @Test
    void updateRejectsBlankFirstNameAfterTrimWithoutTouchingRepository() {
        assertThatThrownBy(() -> personService.update(1L, new PersonRequest("   ", "Mustermann"), 0L))
                .isInstanceOf(ConstraintViolationException.class);

        verifyNoInteractions(personRepository);
    }

    @Test
    void updateRejectsLastNameLongerThan100CharsAfterTrimWithoutTouchingRepository() {
        final String tooLong = "A".repeat(101);

        assertThatThrownBy(() -> personService.update(1L, new PersonRequest("Max", tooLong), 0L))
                .isInstanceOf(ConstraintViolationException.class);

        verifyNoInteractions(personRepository);
    }

    @Test
    void updateReturnsUpdatedPersonOnSuccess() {
        final Person existing = new Person(1L, "Alt", "Name", null, 0L);
        final Person updated = new Person(1L, "Neu", "Name", null, 1L);
        when(personRepository.findById(1L)).thenReturn(java.util.Optional.of(existing), java.util.Optional.of(updated));
        when(personRepository.update(1L, "Neu", "Name", 0L)).thenReturn(1);

        final Person result = personService.update(1L, new PersonRequest("  Neu  ", "  Name  "), 0L);

        assertThat(result).isEqualTo(updated);
        verify(personRepository).update(1L, "Neu", "Name", 0L);
    }

    @Test
    void updateThrowsOptimisticLockExceptionWhenZeroRowsAffected() {
        final Person existing = new Person(1L, "Alt", "Name", null, 0L);
        when(personRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(personRepository.update(1L, "Neu", "Name", 0L)).thenReturn(0);

        assertThatThrownBy(() -> personService.update(1L, new PersonRequest("Neu", "Name"), 0L))
                .isInstanceOf(OptimisticLockException.class);
    }

    @Test
    void updateThrowsPersonNotFoundExceptionForUnknownIdWithoutCallingUpdate() {
        when(personRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> personService.update(999L, new PersonRequest("Max", "Mustermann"), 0L))
                .isInstanceOf(PersonNotFoundException.class);

        verify(personRepository, never()).update(anyLong(), any(), any(), anyLong());
    }
}
