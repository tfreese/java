// Created: 12.03.2015
package de.freese.openstreetmap.io;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.freese.openstreetmap.model.OsmModel;

/**
 * Parser zum Auslesen der XML Kartendaten von <a href="http://www.openstreetmap.org">openstreetmap</a>.<br>
 * Beste Variante, da nur das aktuelle Element im Speicher gehalten wird.
 *
 * @author Thomas Freese
 */
public class SaxOSMParser implements OSMParser {
    @Override
    public OsmModel parse(final InputStream inputStream) throws Exception {
        OsmModel model = new OsmModel();
        OSMContentHandler contentHandler = new OSMContentHandler(model);
        // XMLReader reader = XMLReaderFactory.createXMLReader();
        // reader.setContentHandler(contentHandler);
        // reader.parse(new InputSource(inputStream));

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        SAXParser saxParser = saxParserFactory.newSAXParser();

        // Protect against to XXE attacks.
        saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        //        saxParser.setProperty(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        saxParser.parse(inputStream, contentHandler);

        return model;
    }
}
