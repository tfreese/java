// Created: 03 Apr. 2025
package de.freese.dependency.update.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import de.freese.dependency.utils.Pools;

/**
 * @author Thomas Freese
 */
public abstract class AbstractRepositoryClient implements RepositoryClient {
    protected static List<String> parseVersionsXml(final InputStream inputStream) throws IOException, SAXException, XPathExpressionException {
        final DocumentBuilder documentBuilder = Pools.DOCUMENT_BUILDER.get();
        final XPathFactory xpathFactory = Pools.X_PATH_FACTORY.get();

        try {
            final Document document = documentBuilder.parse(inputStream);
            final XPath xpath = xpathFactory.newXPath();

            // Returns only the latest Version, if Milestone the current release could be ignored.
            // Final XPathExpression expression = xpath.compile("/metadata/versioning/latest");

            // Returns all Versions.
            final XPathExpression expression = xpath.compile("/metadata/versioning/versions/version");

            final NodeList nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

            return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item).map(Node::getTextContent).toList();
        }
        finally {
            Pools.DOCUMENT_BUILDER.free(documentBuilder);
            Pools.X_PATH_FACTORY.free(xpathFactory);
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JsonMapper jsonMapper;

    protected Logger getLogger() {
        return logger;
    }

    protected synchronized ObjectMapper getObjectMapper() {
        if (jsonMapper == null) {
            jsonMapper = JsonMapper.builder()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .build();
        }

        return jsonMapper;
    }

    protected List<String> parseVersionsJson(final String json) {
        final JsonNode jsonNode = getObjectMapper().readValue(json, JsonNode.class);
        // jsonNode = jsonNode.findPath("docs");

        return jsonNode.findValuesAsString("v");
    }
}
