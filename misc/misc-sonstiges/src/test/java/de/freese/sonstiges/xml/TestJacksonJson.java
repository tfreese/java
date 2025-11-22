package de.freese.sonstiges.xml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.json.JsonMapper;

import de.freese.sonstiges.xml.jaxb.model.Club;
import de.freese.sonstiges.xml.jaxb.model.ClubFactory;

/**
 * Links:<br>
 * <a href="http:// www.baeldung.com/jackson-annotations">jackson-annotations</a><br>
 * <a href="http://openbook.galileocomputing.de/javainsel8/javainsel_15_008.htm#mje87729331896b2153f4d617a13dd4666">javainsel_15_008</a><br>
 * <a href="http://www.tutorials.de/forum/java/263489-jaxb-tutorial.html">263489-jaxb-tutorial</a>
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestJacksonJson {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestJacksonJson.class);

    @Test
    void testJson() throws Exception {
        final AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();

        final JsonMapper jsonMapper = JsonMapper.builder()
                .annotationIntrospector(jacksonIntrospector)

                // Name des Root-Objektes mit anzeigen.
                .enable(SerializationFeature.WRAP_ROOT_VALUE)
                .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)

                // Globales PrettyPrinting; oder einzeln über jsonMapper.writerWithDefaultPrettyPrinter() nutzbar.
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .changeDefaultVisibility(handler ->
                        handler.withFieldVisibility(Visibility.NONE)
                                .withSetterVisibility(Visibility.PUBLIC_ONLY)
                                .withGetterVisibility(Visibility.PUBLIC_ONLY))
                .build();

        Club club = ClubFactory.createClub();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (OutputStream os = baos) {
            // jsonMapper.writerWithDefaultPrettyPrinter().writeValue(baos, club);
            jsonMapper.writer().writeValue(os, club);
        }

        final byte[] bytes = baos.toByteArray();
        assertNotNull(bytes);

        LOGGER.info(new String(bytes, StandardCharsets.UTF_8));

        // Reverse
        club = jsonMapper.readValue(bytes, Club.class);
        assertNotNull(club);
        // ClubFactory.toString(club);
    }
}
