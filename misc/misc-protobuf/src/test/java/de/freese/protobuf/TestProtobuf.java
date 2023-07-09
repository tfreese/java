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
public class TestProtobuf {

    private static AddressBook addressBook;

    @BeforeAll
    static void beforeAll() {
        long id = System.currentTimeMillis();
        String name = "Test";
        String email = "test@example.org";

        // @formatter:off
        PhoneNumber.Builder phoneNumberBuilder = PhoneNumber.newBuilder()
                .setType(PhoneType.PHONE_TYPE_HOME)
                .setNumber("007")
                ;

        Person.Builder personBuilder = Person.newBuilder()
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
        Person person = addressBook.getPersons(0);

        Person person1 = Person.newBuilder(person).setName("Dummy").build();

        assertEquals(person.getId(), person1.getId());
        assertEquals(person.getEmail(), person1.getEmail());
        assertEquals("Dummy", person1.getName());
    }

    @Test
    void testSerialisation() throws Exception {
        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        addressBook.writeTo(baos);
        baos.flush();

        // Deserialize
        AddressBook deserialized = AddressBook.parseFrom(baos.toByteArray());
        // AddressBook.newBuilder().mergeFrom(baos.toByteArray()).build();

        assertEquals(addressBook, deserialized);
    }
}
