package de.freese.sonstiges.xml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        final ObjectMapper jsonMapper = new ObjectMapper();

        // final JacksonXmlModule xmlModule = new JacksonXmlModule();
        // xmlModule.setDefaultUseWrapper(false);
        //
        // final ObjectMapper xmlMapper = new XmlMapper(xmlModule);
        // xmlMapper.registerModule(new JaxbAnnotationModule());
        // xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // final AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        // xmlMapper.setAnnotationIntrospector(jaxbIntrospector);
        // xmlMapper.getDeserializationConfig().with(jaxbIntrospector);
        // xmlMapper.getSerializationConfig().with(jaxbIntrospector);

        final AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();

        // // Annotation-Mix: Verwende primär JaxB-Annotations und sekundär Jackson-Annotations
        // final AnnotationIntrospector introspector = new AnnotationIntrospectorPair(jaxbIntrospector, jacksonIntrospector);

        jsonMapper.setAnnotationIntrospector(jacksonIntrospector);
        // jsonMapper.getDeserializationConfig().with(introspector);
        // jsonMapper.getSerializationConfig().with(introspector);

        // Name des Root-Objektes mit anzeigen.
        jsonMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        jsonMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

        // Globales PrettyPrinting; oder einzeln über jsonMapper.writerWithDefaultPrettyPrinter() nutzbar.
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);

        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        // jsonMapper.setVisibility(jsonMapper.getVisibilityChecker().with(Visibility.NONE));
        jsonMapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
        jsonMapper.setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY);
        jsonMapper.setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY);

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
