package de.freese.dependency.utils;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Thomas Freese
 */
public final class Pools {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pools.class);

    private static final EntityResolver BLOCKING_ENTITY_RESOLVER = new BlockingEntityResolver();

    public static final ObjectPool<DocumentBuilder> DOCUMENT_BUILDER = new ObjectPool<>(() -> {
        LOGGER.debug("create DocumentBuilder");

        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(false); // No XML-Schema (xmlns).

            // Protect against to XXE attacks.
            // Verhindert externe DTDs, damit keine externen Ressourcen nachgeladen werden können.
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            // Verhindert externe XML-Schemas, damit kein externer Schema-Zugriff möglich ist.
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            // Aktiviert allgemeine Sicherheitsbeschränkungen für den Parser.
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // Verbietet DOCTYPE-Deklarationen vollständig, um XXE-Angriffsflächen zu schließen.
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // Deaktiviert externe General Entities, damit keine externen Inhalte eingebunden werden.
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            // Deaktiviert externe Parameter Entities, um DTD-basierte Angriffe weiter zu verhindern.
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // Unterbindet das Laden externer DTDs, damit der Parser keine Netz- oder Dateizugriffe ausführt.
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            // Unterbindet das automatische Ersetzen von Entities und reduziert damit Entity-Expansion weiter.
            documentBuilderFactory.setExpandEntityReferences(false);
            // Deaktiviert Validierung, weil sie zusätzliche Parser-Angriffsfläche erzeugen kann.
            documentBuilderFactory.setFeature("http://xml.org/sax/features/validation", false);
            // Deaktiviert XInclude, damit keine externen XML-Fragmente eingebunden werden.
            documentBuilderFactory.setXIncludeAware(false);

            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(BLOCKING_ENTITY_RESOLVER);

            return documentBuilder;
        }
        catch (final ParserConfigurationException ex) {
            throw new IllegalStateException("Unable to create secure DocumentBuilder", ex);
        }
    });

    public static final ObjectPool<XPathFactory> X_PATH_FACTORY = new ObjectPool<>(() -> {
        LOGGER.debug("create XPathFactory");

        final XPathFactory xPathFactory = XPathFactory.newInstance();

        try {
            xPathFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        }
        catch (XPathFactoryConfigurationException ex) {
            throw new IllegalStateException("Unable to configure secure XPathFactory", ex);
        }

        return xPathFactory;
    });

    private static final String JDK_XML_ELEMENT_ATTRIBUTE_LIMIT = "jdk.xml.elementAttributeLimit";
    private static final String JDK_XML_ENTITY_EXPANSION_LIMIT = "jdk.xml.entityExpansionLimit";
    private static final String JDK_XML_ENTITY_REPLACEMENT_LIMIT = "jdk.xml.entityReplacementLimit";
    private static final String JDK_XML_MAX_ELEMENT_DEPTH = "jdk.xml.maxElementDepth";
    private static final String JDK_XML_MAX_GENERAL_ENTITY_SIZE_LIMIT = "jdk.xml.maxGeneralEntitySizeLimit";
    private static final String JDK_XML_MAX_PARAMETER_ENTITY_SIZE_LIMIT = "jdk.xml.maxParameterEntitySizeLimit";
    private static final String JDK_XML_MAX_XML_NAME_LIMIT = "jdk.xml.maxXMLNameLimit";
    private static final String JDK_XML_TOTAL_ENTITY_SIZE_LIMIT = "jdk.xml.totalEntitySizeLimit";
    private static final String JDK_XPATH_EXPR_GRP_LIMIT = "jdk.xml.xpathExprGrpLimit";
    private static final String JDK_XPATH_EXPR_OP_LIMIT = "jdk.xml.xpathExprOpLimit";
    private static final String JDK_XPATH_TOTAL_OP_LIMIT = "jdk.xml.xpathTotalOpLimit";

    private static final class BlockingEntityResolver implements EntityResolver {
        @Override
        public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
            throw new SAXException("External entity resolution is disabled: publicId=" + publicId + ", systemId=" + systemId);
        }
    }

    static {
        // Konservative Defaults gegen DoS-/Billion-Laughs-/XPath-Exhaustion-Angriffe.
        // Begrenzung der Anzahl entitätsbezogener Expansionen, um Entity-Explosionen zu verhindern.
        setSystemPropertyIfAbsent(JDK_XML_ENTITY_EXPANSION_LIMIT, "256");
        // Begrenzung der gesamten Größe aller ersetzten Entitäten, damit XML nicht unkontrolliert wächst.
        setSystemPropertyIfAbsent(JDK_XML_TOTAL_ENTITY_SIZE_LIMIT, "2000000");
        // Begrenzung der Größe einzelner allgemeiner Entitäten, um große Entity-Inhalte zu unterbinden.
        setSystemPropertyIfAbsent(JDK_XML_MAX_GENERAL_ENTITY_SIZE_LIMIT, "100000");
        // Begrenzung der Größe parametrisierter Entitäten, um missbräuchliche DTD-Konstrukte einzudämmen.
        setSystemPropertyIfAbsent(JDK_XML_MAX_PARAMETER_ENTITY_SIZE_LIMIT, "50000");
        // Begrenzung der insgesamt ersetzten Zeichenanzahl, um Speicher- und CPU-Last zu reduzieren.
        setSystemPropertyIfAbsent(JDK_XML_ENTITY_REPLACEMENT_LIMIT, "300000");
        // Begrenzung der Verschachtelungstiefe, um tief verschachtelte XML-Dokumente abzufangen.
        setSystemPropertyIfAbsent(JDK_XML_MAX_ELEMENT_DEPTH, "64");
        // Begrenzung der Anzahl Attribute je Element, um sehr breite Elemente zu verhindern.
        setSystemPropertyIfAbsent(JDK_XML_ELEMENT_ATTRIBUTE_LIMIT, "128");
        // Begrenzung der XML-Namenslänge, um extrem lange Bezeichner abzuwehren.
        setSystemPropertyIfAbsent(JDK_XML_MAX_XML_NAME_LIMIT, "1024");

        // Begrenzung der Gruppenanzahl in XPath-Ausdrücken, um komplexe Ausdrücke einzugrenzen.
        setSystemPropertyIfAbsent(JDK_XPATH_EXPR_GRP_LIMIT, "10");
        // Begrenzung der Operatoranzahl in XPath-Ausdrücken, um teure Auswertungen zu reduzieren.
        setSystemPropertyIfAbsent(JDK_XPATH_EXPR_OP_LIMIT, "100");
        // Begrenzung der gesamten XPath-Operationsanzahl, um Rechenaufwand zu deckeln.
        setSystemPropertyIfAbsent(JDK_XPATH_TOTAL_OP_LIMIT, "10000");
    }

    private static void setSystemPropertyIfAbsent(final String key, final String value) {
        if (System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }

    private Pools() {
        super();
    }
}
