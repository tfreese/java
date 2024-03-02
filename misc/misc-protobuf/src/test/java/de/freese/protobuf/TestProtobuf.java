// Created: 08.07.23
package de.freese.protobuf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.freese.protobuf.model.addressbook.AddressBook;
import de.freese.protobuf.model.person.Person;
import de.freese.protobuf.model.phone.PhoneNumber;
import de.freese.protobuf.model.phone.PhoneType;
import de.freese.protobuf.model.test.Test1;
import de.freese.protobuf.model.test.Test2;

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

        final Person.Builder personBuilder = Person.newBuilder()
                .setId(id)
                .setName(name)
                .setEmail(email)
                .setBirthDay(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000L).setNanos(1)) // Timestamps.fromMillis(System.currentTimeMillis())
                .addPhones(phoneNumberBuilder)
                ;

        addressBook = AddressBook.newBuilder()
                .addPersons(personBuilder)
                .build()
                ;
        // @formatter:on

        System.out.printf("toString():%n%s%n", addressBook);

        try {
            System.out.printf("JsonFormat:%n%s%n", JsonFormat.printer().print(addressBook));
        }
        catch (InvalidProtocolBufferException ex) {
            throw new RuntimeException(ex);
        }
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
    void testChangedSchema() throws Exception {
        final Test1 test1 = Test1.newBuilder().setName("Name1").build();
        final byte[] bytes = test1.toByteArray();

        final Test2 test2 = Test2.parseFrom(bytes);

        assertEquals(test1.getName(), test2.getName());
        assertEquals(0, test2.getAge());
    }

    @Test
    void testSerialisation() throws Exception {
        // Serialize
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            addressBook.writeTo(baos);
            baos.flush();

            // Deserialize
            final AddressBook deserialized = AddressBook.parseFrom(baos.toByteArray());
            // AddressBook.newBuilder().mergeFrom(baos.toByteArray()).build();

            assertEquals(addressBook, deserialized);
        }

        final byte[] bytes = addressBook.toByteArray();
        final AddressBook deserialized = AddressBook.parseFrom(bytes);
        assertEquals(addressBook, deserialized);
    }
}
