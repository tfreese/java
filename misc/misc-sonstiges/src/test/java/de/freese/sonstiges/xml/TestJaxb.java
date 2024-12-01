package de.freese.sonstiges.xml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.xml.jaxb.model.Club;
import de.freese.sonstiges.xml.jaxb.model.ClubFactory;
import de.freese.sonstiges.xml.jaxb.model.DJ;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestJaxb {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestJaxb.class);

    @Test
    void testXml() throws Exception {
        final JAXBContext jaxbContext = JAXBContext.newInstance(Club.class, DJ.class);
        // siehe de/freese/sonstiges/xml/jaxb/model/jaxb.index
        // JAXBContext jaxbContext = JAXBContext.newInstance("de.freese.sonstiges.xml.jaxb.model");

        // com.fasterxml.jackson.dataformat:jackson-dataformat-xml
        // XmlMapper mapper = new XmlMapper();

        Club club = ClubFactory.createClub();

        final Marshaller m = jaxbContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        final byte[] bytes;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            m.marshal(club, baos);

            baos.flush();
            bytes = baos.toByteArray();
        }

        assertNotNull(bytes);

        LOGGER.info(new String(bytes, StandardCharsets.UTF_8));

        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        try (InputStream fis = new ByteArrayInputStream(bytes)) {
            club = (Club) unmarshaller.unmarshal(fis);
            assertNotNull(club);
            // ClubFactory.toString(club);
        }
    }
}
