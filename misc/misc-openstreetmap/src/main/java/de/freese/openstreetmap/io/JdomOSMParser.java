// Created: 06.11.2011
package de.freese.openstreetmap.io;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.freese.openstreetmap.model.OsmModel;
import de.freese.openstreetmap.model.OsmNode;
import de.freese.openstreetmap.model.OsmRelation;
import de.freese.openstreetmap.model.OsmWay;

/**
 * Parser zum Auslesen der XML Kartendaten von <a href="http://www.openstreetmap.org">openstreetmap</a>.<br>
 * Schlechteste Variante, da das gesamte Dokument im Speicher gehalten wird.
 *
 * @author Thomas Freese
 */
public class JdomOSMParser implements OSMParser {
    @Override
    public OsmModel parse(final InputStream inputStream) throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        // to be compliant, completely disable DOCTYPE declaration:
        docBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        // or completely disable external entities declarations:
        docBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        docBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        // or prohibit the use of all protocols by external entities:
        docBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        docBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        Document document = docBuilder.parse(inputStream);
        document.getDocumentElement().normalize();

        OsmModel model = new OsmModel();

        parseNodes(document, model);
        parseWays(document, model);
        parseRelations(document, model);

        inputStream.close();

        return model;
    }

    private void parseNodes(final Document document, final OsmModel model) {
        NodeList nodeList = document.getElementsByTagName("node");

        // Teure Operation.
        int nodeListLength = nodeList.getLength();

        for (int i = 0; i < nodeListLength; i++) {
            Node node = nodeList.item(i);
            NamedNodeMap nodeAttributes = node.getAttributes();

            long id = Long.parseLong(nodeAttributes.getNamedItem("id").getNodeValue());
            float latitude = Float.parseFloat(nodeAttributes.getNamedItem("lat").getNodeValue());
            float longitude = Float.parseFloat(nodeAttributes.getNamedItem("lon").getNodeValue());

            OsmNode osmNode = new OsmNode();
            osmNode.setID(id);
            osmNode.setLatitude(latitude);
            osmNode.setLongitude(longitude);
            model.getNodeMap().put(id, osmNode);

            // double mX = Mercator.mercX(longitude);
            // double mY = Mercator.mercY(latitude);
            // System.out.println(mX + "- " + mY);

            // Tags auslesen.
            NodeList nodeChildList = node.getChildNodes();

            // Teure Operation.
            int nodeChildListLength = nodeChildList.getLength();

            for (int j = 0; j < nodeChildListLength; j++) {
                Node childNode = nodeChildList.item(j);
                nodeAttributes = childNode.getAttributes();
                String nodeName = childNode.getNodeName();

                if ("tag".equals(nodeName)) {
                    String key = nodeAttributes.getNamedItem("k").getNodeValue();
                    String value = nodeAttributes.getNamedItem("v").getNodeValue();

                    osmNode.getTags().put(key, value);
                }
            }
        }
    }

    private void parseRelations(final Document document, final OsmModel model) {
        NodeList nodeList = document.getElementsByTagName("relation");

        // Teure Operation.
        int nodeListLength = nodeList.getLength();

        for (int i = 0; i < nodeListLength; i++) {
            Node node = nodeList.item(i);
            NamedNodeMap nodeAttributes = node.getAttributes();

            long id = Long.parseLong(nodeAttributes.getNamedItem("id").getNodeValue());

            OsmRelation osmRelation = new OsmRelation();
            osmRelation.setID(id);
            model.getRelationMap().put(id, osmRelation);

            // Tags und Refs auslesen.
            NodeList nodeChildList = node.getChildNodes();

            // Teure Operation.
            int nodeChildListLength = nodeChildList.getLength();

            for (int j = 0; j < nodeChildListLength; j++) {
                Node childNode = nodeChildList.item(j);
                nodeAttributes = childNode.getAttributes();
                String nodeName = childNode.getNodeName();

                if ("tag".equals(nodeName)) {
                    String key = nodeAttributes.getNamedItem("k").getNodeValue();
                    String value = nodeAttributes.getNamedItem("v").getNodeValue();

                    osmRelation.getTags().put(key, value);
                }
                else if ("member".equals(nodeName)) {
                    String type = nodeAttributes.getNamedItem("type").getNodeValue();
                    long refID = Long.parseLong(nodeAttributes.getNamedItem("ref").getNodeValue());

                    if ("node".equals(type)) {
                        OsmNode refNode = model.getNodeMap().get(refID);

                        if (refNode != null) {
                            osmRelation.getNodes().add(refNode);
                        }
                    }
                    else if ("way".equals(type)) {
                        OsmWay refWay = model.getWayMap().get(refID);

                        if (refWay != null) {
                            osmRelation.getWays().add(refWay);
                        }
                    }
                }
            }
        }
    }

    private void parseWays(final Document document, final OsmModel model) {
        NodeList nodeList = document.getElementsByTagName("way");

        // Teure Operation.
        int nodeListLength = nodeList.getLength();

        for (int i = 0; i < nodeListLength; i++) {
            Node node = nodeList.item(i);
            NamedNodeMap nodeAttributes = node.getAttributes();

            long id = Long.parseLong(nodeAttributes.getNamedItem("id").getNodeValue());

            OsmWay osmWay = new OsmWay();
            osmWay.setID(id);
            model.getWayMap().put(id, osmWay);

            // Tags und Refs auslesen.
            NodeList nodeChildList = node.getChildNodes();

            // Teure Operation.
            int nodeChildListLength = nodeChildList.getLength();

            for (int j = 0; j < nodeChildListLength; j++) {
                Node childNode = nodeChildList.item(j);
                nodeAttributes = childNode.getAttributes();
                String nodeName = childNode.getNodeName();

                if ("tag".equals(nodeName)) {
                    String key = nodeAttributes.getNamedItem("k").getNodeValue();
                    String value = nodeAttributes.getNamedItem("v").getNodeValue();

                    osmWay.getTags().put(key, value);
                }
                else if ("nd".equals(nodeName)) {
                    long refID = Long.parseLong(nodeAttributes.getNamedItem("ref").getNodeValue());
                    OsmNode refNode = model.getNodeMap().get(refID);

                    if (refNode != null) {
                        osmWay.getNodes().add(refNode);
                    }
                }
            }
        }
    }
}
