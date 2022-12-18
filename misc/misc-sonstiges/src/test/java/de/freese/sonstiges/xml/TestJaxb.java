package de.freese.sonstiges.xml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import de.freese.sonstiges.xml.jaxb.model.Club;
import de.freese.sonstiges.xml.jaxb.model.ClubFactory;
import de.freese.sonstiges.xml.jaxb.model.DJ;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestJaxb
{
    private static byte[] bytes;

    private static JAXBContext jaxbContext;

    @BeforeAll
    static void beforeAll() throws Exception
    {
        TestJaxb.jaxbContext = JAXBContext.newInstance(Club.class, DJ.class);

        // siehe de/freese/sonstiges/xml/jaxb/model/jaxb.index
        // TestJaxb.jaxbContext = JAXBContext.newInstance("de.freese.sonstiges.xml.jaxb.model");

        // com.fasterxml.jackson.dataformat:jackson-dataformat-xml
        // XmlMapper mapper = new XmlMapper();
    }

    @Test
    @Order(2)
    void testFromXML() throws Exception
    {
        System.out.println(new String(TestJaxb.bytes, StandardCharsets.UTF_8));

        Unmarshaller unmarshaller = TestJaxb.jaxbContext.createUnmarshaller();

        try (InputStream fis = new ByteArrayInputStream(TestJaxb.bytes))
        {
            Club club = (Club) unmarshaller.unmarshal(fis);
            assertNotNull(club);
            // ClubFactory.toString(club);
        }
    }

    @Test
    @Order(1)
    void testToXML() throws Exception
    {
        Club club = ClubFactory.createClub();

        Marshaller m = TestJaxb.jaxbContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            m.marshal(club, baos);

            baos.flush();
            TestJaxb.bytes = baos.toByteArray();
        }

        assertNotNull(TestJaxb.bytes);

        System.out.println(new String(TestJaxb.bytes, StandardCharsets.UTF_8));
    }
}
