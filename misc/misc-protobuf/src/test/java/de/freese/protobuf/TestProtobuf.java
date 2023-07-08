// Created: 08.07.23
package de.freese.protobuf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.freese.protobuf.model.AddressBook;
import de.freese.protobuf.model.Person;
import de.freese.protobuf.model.PhoneNumber;
import de.freese.protobuf.model.PhoneType;

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
        PhoneNumber phoneNumber = PhoneNumber.newBuilder()
                .setType(PhoneType.PHONE_TYPE_HOME)
                .setNumber("007")
                .build()
                ;

        Person person = Person.newBuilder()
                .setId(id)
                .setName(name)
                .setEmail(email)
                .addPhones(phoneNumber)
                .build()
                ;

        addressBook = AddressBook.newBuilder()
                .addPersons(person)
                .build()
                ;
        // @formatter:on

        System.out.println(addressBook);
    }

    @Test
    void testSerialisation() throws Exception {
        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        addressBook.writeTo(baos);
        baos.flush();

        // Deserialize
        AddressBook deserialized = AddressBook.newBuilder().mergeFrom(baos.toByteArray()).build();

        assertEquals(addressBook, deserialized);
    }
}
