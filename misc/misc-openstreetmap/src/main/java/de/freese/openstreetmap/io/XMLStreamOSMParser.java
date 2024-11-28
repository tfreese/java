// Created: 12.03.2015
package de.freese.openstreetmap.io;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.freese.openstreetmap.model.OsmModel;
import de.freese.openstreetmap.model.OsmNode;
import de.freese.openstreetmap.model.OsmRelation;
import de.freese.openstreetmap.model.OsmWay;

/**
 * Parser zum Auslesen der XML-Kartendaten von <a href="http://www.openstreetmap.org">openstreetmap</a>.<br>
 * Beste Variante, da nur das aktuelle Element im Speicher gehalten wird.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("java:S2259")
public class XMLStreamOSMParser implements OSMParser {
    private static final String ATTR_NAME_ID = "id";
    private static final String ATTR_NAME_KEY = "k";
    private static final String ATTR_NAME_LAT = "lat";
    private static final String ATTR_NAME_LON = "lon";
    private static final String ATTR_NAME_REF = "ref";
    private static final String ATTR_NAME_TYPE = "type";
    private static final String ATTR_NAME_VALUE = "v";
    private static final String NODE_NAME_NODE = "node";
    private static final String NODE_NAME_RELATION = "relation";
    private static final String NODE_NAME_RELATIONMEMBER = "member";
    private static final String NODE_NAME_TAG = "tag";
    private static final String NODE_NAME_WAY = "way";
    private static final String NODE_NAME_WAYNODE = "nd";

    private OsmNode node;
    private OsmRelation relation;
    private OsmWay way;

    @Override
    public OsmModel parse(final InputStream inputStream) throws Exception {
        // final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // final Schema schema = schemaFactory.newSchema(schemaFile);
        // Validator validator = schema.newValidator();
        // validator.validate(xmlFile);

        final XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        // Protect against to XXE attacks.
        inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        //        inputFactory.setProperty(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        final XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);

        return parseDocument(reader);
    }

    private OsmModel parseDocument(final XMLStreamReader reader) throws XMLStreamException {
        final OsmModel model = new OsmModel();

        while (reader.hasNext()) {
            final int event = reader.next();

            String localName = null;

            // for (int i = 0; i < reader.getAttributeCount(); i++) {
            // System.out.printf("%d: AttributeLocalName=%s, AttributeValue=%s%n", Integer.valueOf(i), reader.getAttributeLocalName(i),
            // reader.getAttributeValue(i));
            // }

            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    localName = reader.getLocalName();

                    if (NODE_NAME_TAG.equals(localName)) {
                        final String key = reader.getAttributeValue(null, ATTR_NAME_KEY);
                        final String value = reader.getAttributeValue(null, ATTR_NAME_VALUE);

                        if (this.node != null) {
                            this.node.getTags().put(key, value);
                        }
                        else if (this.way != null) {
                            this.way.getTags().put(key, value);
                        }
                        else if (this.relation != null) {
                            this.relation.getTags().put(key, value);
                        }
                    }
                    else if (NODE_NAME_NODE.equals(localName)) {
                        final long id = Long.parseLong(reader.getAttributeValue(null, ATTR_NAME_ID));
                        final float lat = Float.parseFloat(reader.getAttributeValue(null, ATTR_NAME_LAT));
                        final float lon = Float.parseFloat(reader.getAttributeValue(null, ATTR_NAME_LON));

                        this.node = new OsmNode();
                        this.node.setID(id);
                        this.node.setLatitude(lat);
                        this.node.setLongitude(lon);
                        model.getNodeMap().put(id, this.node);
                    }
                    else if (NODE_NAME_WAY.equals(localName)) {
                        final long id = Long.parseLong(reader.getAttributeValue(null, ATTR_NAME_ID));

                        this.way = new OsmWay();
                        this.way.setID(id);
                        model.getWayMap().put(id, this.way);
                    }
                    else if (NODE_NAME_WAYNODE.equals(localName)) {
                        final long refID = Long.parseLong(reader.getAttributeValue(null, ATTR_NAME_REF));

                        final OsmNode n = model.getNodeMap().get(refID);

                        if (n != null) {
                            this.way.getNodes().add(n);
                        }
                    }
                    else if (NODE_NAME_RELATION.equals(localName)) {
                        final long id = Long.parseLong(reader.getAttributeValue(null, ATTR_NAME_ID));

                        this.relation = new OsmRelation();
                        this.relation.setID(id);
                        model.getRelationMap().put(id, this.relation);
                    }
                    else if (NODE_NAME_RELATIONMEMBER.equals(localName)) {
                        final String type = reader.getAttributeValue(null, ATTR_NAME_TYPE);
                        final long refID = Long.parseLong(reader.getAttributeValue(null, ATTR_NAME_REF));

                        if (NODE_NAME_NODE.equals(type)) {
                            final OsmNode refNode = model.getNodeMap().get(refID);

                            if (refNode != null) {
                                this.relation.getNodes().add(refNode);
                            }
                        }
                        else if (NODE_NAME_WAY.equals(type)) {
                            final OsmWay refWay = model.getWayMap().get(refID);

                            if (refWay != null) {
                                this.relation.getWays().add(refWay);
                            }
                        }
                    }

                    break;

                case XMLStreamConstants.END_ELEMENT:
                    localName = reader.getLocalName();

                    if (NODE_NAME_NODE.equals(localName)) {
                        this.node = null;
                    }
                    else if (NODE_NAME_WAY.equals(localName)) {
                        this.way = null;
                    }
                    else if (NODE_NAME_RELATION.equals(localName)) {
                        this.relation = null;
                    }

                    break;
                default:
                    break;
            }
        }

        return model;
    }
}
