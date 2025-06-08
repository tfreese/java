// Created: 12.03.2015
package de.freese.openstreetmap.io;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.freese.openstreetmap.model.OsmModel;
import de.freese.openstreetmap.model.OsmNode;
import de.freese.openstreetmap.model.OsmRelation;
import de.freese.openstreetmap.model.OsmWay;

/**
 * XML-{@link ContentHandler} for OSM-Dateien.
 *
 * @author Thomas Freese
 */
public class OSMContentHandler extends DefaultHandler {
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

    private final OsmModel osmModel;

    private OsmNode node;
    private OsmRelation relation;
    private OsmWay way;

    public OSMContentHandler(final OsmModel osmModel) {
        super();

        this.osmModel = osmModel;
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (NODE_NAME_NODE.equals(localName)) {
            node = null;
        }
        else if (NODE_NAME_WAY.equals(localName)) {
            way = null;
        }
        else if (NODE_NAME_RELATION.equals(localName)) {
            relation = null;
        }
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (NODE_NAME_TAG.equals(localName)) {
            final String key = attributes.getValue(ATTR_NAME_KEY);
            final String value = attributes.getValue(ATTR_NAME_VALUE);

            if (node != null) {
                node.getTags().put(key, value);
            }
            else if (way != null) {
                way.getTags().put(key, value);
            }
            else if (relation != null) {
                relation.getTags().put(key, value);
            }
        }
        else if (NODE_NAME_NODE.equals(localName)) {
            final long id = Long.parseLong(attributes.getValue(ATTR_NAME_ID));
            final float lat = Float.parseFloat(attributes.getValue(ATTR_NAME_LAT));
            final float lon = Float.parseFloat(attributes.getValue(ATTR_NAME_LON));

            node = new OsmNode();
            node.setID(id);
            node.setLatitude(lat);
            node.setLongitude(lon);
            osmModel.getNodeMap().put(id, node);
        }
        else if (NODE_NAME_WAY.equals(localName)) {
            final long id = Long.parseLong(attributes.getValue(ATTR_NAME_ID));

            way = new OsmWay();
            way.setID(id);
            osmModel.getWayMap().put(id, way);
        }
        else if (NODE_NAME_WAYNODE.equals(localName)) {
            final long refID = Long.parseLong(attributes.getValue(ATTR_NAME_REF));

            final OsmNode n = osmModel.getNodeMap().get(refID);

            if (n != null) {
                way.getNodes().add(n);
            }
        }
        else if (NODE_NAME_RELATION.equals(localName)) {
            final long id = Long.parseLong(attributes.getValue(ATTR_NAME_ID));

            relation = new OsmRelation();
            relation.setID(id);
            osmModel.getRelationMap().put(id, relation);
        }
        else if (NODE_NAME_RELATIONMEMBER.equals(localName)) {
            final String type = attributes.getValue(ATTR_NAME_TYPE);
            final long refID = Long.parseLong(attributes.getValue(ATTR_NAME_REF));

            if (NODE_NAME_NODE.equals(type)) {
                final OsmNode refNode = osmModel.getNodeMap().get(refID);

                if (refNode != null) {
                    relation.getNodes().add(refNode);
                }
            }
            else if (NODE_NAME_WAY.equals(type)) {
                final OsmWay refWay = osmModel.getWayMap().get(refID);

                if (refWay != null) {
                    relation.getWays().add(refWay);
                }
            }
        }
    }
}
