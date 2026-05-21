// Created: 28.05.23
package de.freese.dependency.update.property;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.freese.dependency.utils.Pools;

/**
 * @author Thomas Freese
 */
final class PropertySupplierIvySettings implements PropertySupplier {
    private final Path path;

    PropertySupplierIvySettings(final Path path) {
        super();

        this.path = Objects.requireNonNull(path, "path required");
    }

    @Override
    public Map<String, String> get() {
        final Map<String, String> map = new TreeMap<>();

        final DocumentBuilder documentBuilder = Pools.DOCUMENT_BUILDER.get();
        final XPathFactory xpathFactory = Pools.X_PATH_FACTORY.get();

        try (InputStream inputStream = Files.newInputStream(path)) {
            final Document document = documentBuilder.parse(inputStream);
            final XPath xpath = xpathFactory.newXPath();

            final XPathExpression expression = xpath.compile("/ivysettings/property");

            final NodeList nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);

                final NamedNodeMap nodeMap = node.getAttributes();

                final String key = nodeMap.getNamedItem("name").getNodeValue();
                final String value = nodeMap.getNamedItem("value").getNodeValue();

                map.put(key, value);
            }
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {
            Pools.DOCUMENT_BUILDER.free(documentBuilder);
            Pools.X_PATH_FACTORY.free(xpathFactory);
        }

        return map;
    }
}
