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
public class SaxOSMParser implements OSMParser
{
    /**
     * @see de.freese.openstreetmap.io.OSMParser#parse(java.io.InputStream)
     */
    @Override
    public OsmModel parse(final InputStream inputStream) throws Exception
    {
        OsmModel model = new OsmModel();
        OSMContentHandler contentHandler = new OSMContentHandler(model);
        // XMLReader reader = XMLReaderFactory.createXMLReader();
        // reader.setContentHandler(contentHandler);
        // reader.parse(new InputSource(inputStream));

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        // to be compliant, completely disable DOCTYPE declaration:
        saxParserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        // or completely disable external entities declarations:
        saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        // or prohibit the use of all protocols by external entities:

        SAXParser saxParser = saxParserFactory.newSAXParser();
        saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        saxParser.parse(inputStream, contentHandler);

        return model;
    }
}
