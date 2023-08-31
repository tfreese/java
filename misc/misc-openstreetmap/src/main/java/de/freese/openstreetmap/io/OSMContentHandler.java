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
            this.node = null;
        }
        else if (NODE_NAME_WAY.equals(localName)) {
            this.way = null;
        }
        else if (NODE_NAME_RELATION.equals(localName)) {
            this.relation = null;
        }
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (NODE_NAME_TAG.equals(localName)) {
            String key = attributes.getValue(ATTR_NAME_KEY);
            String value = attributes.getValue(ATTR_NAME_VALUE);

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
            long id = Long.parseLong(attributes.getValue(ATTR_NAME_ID));
            float lat = Float.parseFloat(attributes.getValue(ATTR_NAME_LAT));
            float lon = Float.parseFloat(attributes.getValue(ATTR_NAME_LON));

            this.node = new OsmNode();
            this.node.setID(id);
            this.node.setLatitude(lat);
            this.node.setLongitude(lon);
            this.osmModel.getNodeMap().put(id, this.node);
        }
        else if (NODE_NAME_WAY.equals(localName)) {
            long id = Long.parseLong(attributes.getValue(ATTR_NAME_ID));

            this.way = new OsmWay();
            this.way.setID(id);
            this.osmModel.getWayMap().put(id, this.way);
        }
        else if (NODE_NAME_WAYNODE.equals(localName)) {
            long refID = Long.parseLong(attributes.getValue(ATTR_NAME_REF));

            OsmNode n = this.osmModel.getNodeMap().get(refID);

            if (n != null) {
                this.way.getNodes().add(n);
            }
        }
        else if (NODE_NAME_RELATION.equals(localName)) {
            long id = Long.parseLong(attributes.getValue(ATTR_NAME_ID));

            this.relation = new OsmRelation();
            this.relation.setID(id);
            this.osmModel.getRelationMap().put(id, this.relation);
        }
        else if (NODE_NAME_RELATIONMEMBER.equals(localName)) {
            String type = attributes.getValue(ATTR_NAME_TYPE);
            long refID = Long.parseLong(attributes.getValue(ATTR_NAME_REF));

            if (NODE_NAME_NODE.equals(type)) {
                OsmNode refNode = this.osmModel.getNodeMap().get(refID);

                if (refNode != null) {
                    this.relation.getNodes().add(refNode);
                }
            }
            else if (NODE_NAME_WAY.equals(type)) {
                OsmWay refWay = this.osmModel.getWayMap().get(refID);

                if (refWay != null) {
                    this.relation.getWays().add(refWay);
                }
            }
        }
    }
}
