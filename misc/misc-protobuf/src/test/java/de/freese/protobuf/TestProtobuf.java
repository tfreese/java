// Created: 08.07.23
package de.freese.protobuf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.freese.protobuf.model.addressbook.AddressBook;
import de.freese.protobuf.model.person.Person;
import de.freese.protobuf.model.phone.PhoneNumber;
import de.freese.protobuf.model.phone.PhoneType;
import de.freese.protobuf.model.test.ListOfTest1;
import de.freese.protobuf.model.test.ListOfTest2;
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

        final PhoneNumber.Builder phoneNumberBuilder = PhoneNumber.newBuilder()
                .setType(PhoneType.PHONE_TYPE_HOME)
                .setNumber("007");

        final Person.Builder personBuilder = Person.newBuilder()
                .setId(id)
                .setName(name)
                .setEmail(email)
                .setBirthDay(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000L).setNanos(1)) // Timestamps.fromMillis(System.currentTimeMillis())
                .addPhones(phoneNumberBuilder);

        addressBook = AddressBook.newBuilder()
                .addPersons(personBuilder)
                .build();

        System.out.printf("toString():%n%s%n", addressBook);

        try {
            System.out.printf("JsonFormat:%n%s%n", JsonFormat.printer().sortingMapKeys().print(addressBook));
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

    /**
     * The Protocol Buffer wire format is not self-delimiting.<br>
     * For multiple Objects, use a Wrapper-Object with "repeated" tag.<br>
     * Otherwise, write the Object-Size before the Object itself and parse it programmatically.
     * <a href="https://protobuf.dev/programming-guides/techniques">Programming-Guides</a>
     */
    @Test
    void testMultipleObjects() throws Exception {
        byte[] bytes;

        // Wrapper-Object
        final ListOfTest1 listOfTest1Origin = ListOfTest1.newBuilder().addTest(Test1.newBuilder().setName("Name11")).addTest(Test1.newBuilder().setName("Name2")).build();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            listOfTest1Origin.writeTo(baos);

            baos.flush();
            bytes = baos.toByteArray();
        }

        final ListOfTest1 listOfTest1Copy = ListOfTest1.parseFrom(bytes);
        assertNotNull(listOfTest1Copy);
        assertEquals(listOfTest1Origin, listOfTest1Copy);

        final ListOfTest2 listOfTest2Copy = ListOfTest2.parseFrom(bytes);
        assertNotNull(listOfTest2Copy);
        assertEquals(listOfTest1Origin.getTestCount(), listOfTest2Copy.getTestCount());

        // Object-Size before the Object.
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (int i = 1; i <= 2; i++) {
                final Test1 test = Test1.newBuilder().setName("Name" + i).build();
                final int serializedSize = test.getSerializedSize();

                bytes = new byte[4];
                bytes[0] = (byte) (0xFF & (serializedSize >> 24));
                bytes[1] = (byte) (0xFF & (serializedSize >> 16));
                bytes[2] = (byte) (0xFF & (serializedSize >> 8));
                bytes[3] = (byte) (0xFF & serializedSize);

                baos.write(bytes);
                test.writeTo(baos);
            }

            baos.flush();

            bytes = baos.toByteArray();
        }

        final List<Test1> list1 = new ArrayList<>();
        final List<Test2> list2 = new ArrayList<>();

        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            while (inputStream.available() > 0) {
                // Stream is read, but only the last Object is returned.
                // list1.add(Test1.parseFrom(inputStream));
                // list1.add(Test1.newBuilder().mergeFrom(inputStream).build());
                bytes = new byte[4];
                inputStream.read(bytes);

                final int serializedSize = ((bytes[0] & 0xFF) << 24) + ((bytes[1] & 0xFF) << 16) + ((bytes[2] & 0xFF) << 8) + (bytes[3] & 0xFF);
                bytes = new byte[serializedSize];
                inputStream.read(bytes);

                list1.add(Test1.parseFrom(bytes));
                list2.add(Test2.parseFrom(bytes));
            }
        }

        assertFalse(list1.isEmpty());
        assertEquals(2, list1.size());

        assertFalse(list2.isEmpty());
        assertEquals(2, list2.size());
    }

    @Test
    void testSerialisation() throws Exception {
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
