// Created: 28.05.23
package de.freese.dependency.update.coordinate;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

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
import de.freese.dependency.utils.Utils;

/**
 * @author Thomas Freese
 */
final class CoordinateSupplierIvy implements CoordinateSupplier {
    private final Path path;

    CoordinateSupplierIvy(final Path path) {
        super();

        this.path = Objects.requireNonNull(path, "path required");
    }

    @Override
    public List<Coordinate> get() {
        final List<Coordinate> coordinates = new ArrayList<>();

        final DocumentBuilder documentBuilder = Pools.DOCUMENT_BUILDER.get();
        final XPathFactory xpathFactory = Pools.X_PATH_FACTORY.get();

        try (InputStream inputStream = Files.newInputStream(path)) {
            final Document document = documentBuilder.parse(inputStream);
            final XPath xpath = xpathFactory.newXPath();

            final XPathExpression expression = xpath.compile("/ivy-module/dependencies/dependency");

            final NodeList nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);

                final NamedNodeMap nodeMap = node.getAttributes();

                if (nodeMap.getNamedItem("org") == null) {
                    continue;
                }

                final String groupId = nodeMap.getNamedItem("org").getNodeValue();
                final String artifactId = nodeMap.getNamedItem("name").getNodeValue();
                final String version = nodeMap.getNamedItem("rev").getNodeValue();

                coordinates.add(new Coordinate(groupId, artifactId, version, Utils.toSource(path)));
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

        return coordinates;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CoordinateSupplierIvy.class.getSimpleName() + "[", "]")
                .add("path=" + path)
                .toString();
    }
}
