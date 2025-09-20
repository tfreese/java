// Created: 02.04.2009
package de.freese.sonstiges.xml;

import java.io.File;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

/**
 * @author Thomas Freese
 */
public final class NavigationTreeReaderMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationTreeReaderMain.class);

    static void main() throws Exception {
        try {
            URL url = ClassLoader.getSystemResource("navigationTree.xsd");
            final Source schemaFile = new StreamSource(new File(url.toURI()));

            url = ClassLoader.getSystemResource("navigationTree.xml");
            final Source xmlFile = new StreamSource(new File(url.toURI()));

            // Validate Schema.
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");

            final Schema schema = schemaFactory.newSchema(schemaFile);
            final Validator validator = schema.newValidator();
            validator.validate(xmlFile);

            // System.setProperty("javax.xml.stream.XMLInputFactory", value) ;

            // Stax Parsing.
            final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            final XMLStreamReader reader = inputFactory.createXMLStreamReader(xmlFile);

            parseDocument(reader);

            reader.close();
        }
        catch (SAXParseException ex) {
            LOGGER.error("SAXParseException at Line: {}", ex.getLineNumber());
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static void parseDocument(final XMLStreamReader reader) throws XMLStreamException {
        LOGGER.info("Version: {}", reader.getVersion());
        LOGGER.info("Is Standalone: {}", reader.isStandalone());
        LOGGER.info("Standalone Set: {}", reader.standaloneSet());
        LOGGER.info("Encoding: {}", reader.getEncoding());
        LOGGER.info("CharacterEncodingScheme: {}", reader.getCharacterEncodingScheme());

        parseRestOfDocument(reader);
    }

    private static void parseRestOfDocument(final XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            final int type = reader.next();

            switch (type) {
                case XMLStreamConstants.START_ELEMENT -> {
                    LOGGER.info("NamespaceURI: {}", reader.getNamespaceURI());
                    LOGGER.info("START_ELEMENT: {}", reader.getLocalName());
                    LOGGER.info("Prefix: {}", reader.getPrefix());
                    LOGGER.info("AttributeCount: {}", reader.getAttributeCount());

                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        LOGGER.info("{}: AttributeLocalName={}, AttributeValue={}, AttributePrefix={}", i, reader.getAttributeLocalName(i), reader.getAttributeValue(i),
                                reader.getAttributePrefix(i));
                    }
                }

                case XMLStreamConstants.END_ELEMENT -> LOGGER.info("END_ELEMENT");
                case XMLStreamConstants.CHARACTERS -> LOGGER.info("CHARACTERS={}", reader.isWhiteSpace() ? "" : reader.getText());
                case XMLStreamConstants.SPACE -> LOGGER.info("SPACE");
                case XMLStreamConstants.COMMENT -> LOGGER.info("COMMENT={}", reader.getText());
                case XMLStreamConstants.END_DOCUMENT -> LOGGER.info("END_DOCUMENT");
                default -> throw new IllegalStateException("unsupported stax type:" + type);
            }
        }
    }

    private NavigationTreeReaderMain() {
        super();
    }
}
