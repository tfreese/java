// Created: 08.07.23
package de.freese.protobuf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.freese.protobuf.model.addressbook.AddressBook;
import de.freese.protobuf.model.person.Person;
import de.freese.protobuf.model.phone.PhoneNumber;
import de.freese.protobuf.model.phone.PhoneType;

/**
 * @author Thomas Freese
 */
class TestProtobuf {

    private static AddressBook addressBook;

    @BeforeAll
    static void beforeAll() {
        final long id = System.currentTimeMillis();
        final String name = "Test";
        final String email = "test@example.org";

        // @formatter:off
        final PhoneNumber.Builder phoneNumberBuilder = PhoneNumber.newBuilder()
                .setType(PhoneType.PHONE_TYPE_HOME)
                .setNumber("007")
                ;

        final  Person.Builder personBuilder = Person.newBuilder()
                .setId(id)
                .setName(name)
                .setEmail(email)
                .setBirthDay(Timestamp.newBuilder().setSeconds(System.currentTimeMillis()).setNanos(1))
                .addPhones(phoneNumberBuilder)
                ;

        addressBook = AddressBook.newBuilder()
                .addPersons(personBuilder)
                .build()
                ;
        // @formatter:on

        System.out.println(addressBook);
    }

    @Test
    void testChangeValue() {
        final Person person = addressBook.getPersons(0);

        final Person person1 = Person.newBuilder(person).setName("Dummy").build();

        assertEquals(person.getId(), person1.getId());
        assertEquals(person.getEmail(), person1.getEmail());
        assertEquals("Dummy", person1.getName());
    }

    @Test
    void testSerialisation() throws Exception {
        // Serialize
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        addressBook.writeTo(baos);
        baos.flush();

        // Deserialize
        final AddressBook deserialized = AddressBook.parseFrom(baos.toByteArray());
        // AddressBook.newBuilder().mergeFrom(baos.toByteArray()).build();

        assertEquals(addressBook, deserialized);
    }
}
