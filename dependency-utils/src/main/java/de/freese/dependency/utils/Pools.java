package de.freese.dependency.utils;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class Pools {
    public static final ObjectPool<DocumentBuilder> DOCUMENT_BUILDER = new ObjectPool<>(() -> {
        LoggerFactory.getLogger("DocumentBuilderPool").debug("create DocumentBuilder");

        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(false); // No XML-Schema (xmlns).

            // Protect against to XXE attacks.
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            return documentBuilderFactory.newDocumentBuilder();
        }
        catch (final ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    });

    public static final ObjectPool<XPathFactory> X_PATH_FACTORY = new ObjectPool<>(XPathFactory::newInstance);

    private Pools() {
        super();
    }
}
