// Created: 06.03.2019
package de.freese.dependency.update.version.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * {@link VersionFilter} for a Maven RuleSet.
 *
 * @author Thomas Freese
 */
final class VersionFilterMavenRuleSet implements VersionFilter {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, Predicate<String>> rules = new TreeMap<>();

    private Predicate<String> ignoreVersions = v -> false;

    VersionFilterMavenRuleSet() {
        super();
    }

    @Override
    public Set<String> getFilteredVersions(final String groupId, final String artifactId, final Collection<String> versions) {
        String key = groupId + ":";

        if (artifactId == null || artifactId.isBlank()) {
            key += "*";
        } else {
            key += artifactId;
        }

        Predicate<String> rule = rules.get(key);

        if (rule == null) {
            // Fallback: starry out thew ArtifactId
            key = groupId + ":*";
            rule = rules.get(key);
        }

        if (rule == null) {
            // Fallback: Use '*' for GroupId
            String g = groupId;

            while (g.contains(".")) {
                g = g.substring(0, g.lastIndexOf('.'));

                key = g + ".*:*";
                rule = rules.get(key);

                if (rule != null) {
                    break;
                }
            }
        }

        Predicate<String> filter = ignoreVersions;

        if (rule != null) {
            filter = ignoreVersions.or(rule);
        }

        return versions.stream().filter(filter.negate()).collect(Collectors.toCollection(TreeSet::new));
    }

    void parse(final Path ruleSet) {
        try (InputStream is = Files.newInputStream(ruleSet)) {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(false); // No XML-Schema (xmlns).

            // Protect against to XXE attacks.
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(is);

            final XPathFactory xpathFactory = XPathFactory.newInstance();

            parseIgnoreVersions(document, xpathFactory);
            parseRules(document, xpathFactory);
        }
        catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
        catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Logger getLogger() {
        return logger;
    }

    private void parseIgnoreVersions(final Document document, final XPathFactory xpathFactory) throws XPathExpressionException {
        final XPath xpath = xpathFactory.newXPath();
        // XPathExpression expr = xpath.compile("/ruleset/ignoreVersions/ignoreVersion[@type='regex']/text()");
        final XPathExpression expr = xpath.compile("/ruleset/ignoreVersions/ignoreVersion");
        final NodeList result = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        for (int i = 0; i < result.getLength(); i++) {
            final Element nodeIgnoreVersion = (Element) result.item(i);

            final String type = nodeIgnoreVersion.getAttribute("type");
            final String ignoreVersion = nodeIgnoreVersion.getTextContent();

            getLogger().debug("type = {}", ignoreVersion);

            if ("regex".equals(type)) {
                ignoreVersions = ignoreVersions.or(v -> v.matches(ignoreVersion));
            } else if ("exact".equals(type)) {
                ignoreVersions = ignoreVersions.or(v -> v.equals(ignoreVersion));
            } else {
                getLogger().warn("Unsupported ignore version type: {}", type);
            }
        }
    }

    private void parseRules(final Document document, final XPathFactory xpathFactory) throws XPathExpressionException {
        final XPath xpath = xpathFactory.newXPath();
        final XPathExpression expr = xpath.compile("/ruleset/rules/rule");
        final NodeList result = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        for (int i = 0; i < result.getLength(); i++) {
            final Element nodeRule = (Element) result.item(i);

            final String groupId = nodeRule.getAttribute("groupId");
            String artifactId = nodeRule.getAttribute("artifactId");

            if (artifactId.isBlank()) {
                artifactId = "*";
            }

            final String key = groupId + ":" + artifactId;

            final Element nodeIgnoreVersions = (Element) nodeRule.getElementsByTagName("ignoreVersions").item(0);
            final NodeList ruleIgnoreVersions = nodeIgnoreVersions.getElementsByTagName("ignoreVersion");

            for (int j = 0; j < ruleIgnoreVersions.getLength(); j++) {
                final Element nodeIgnoreVersion = (Element) ruleIgnoreVersions.item(j);

                final String type = nodeIgnoreVersion.getAttribute("type");
                final String ignoreVersion = nodeIgnoreVersion.getTextContent();

                getLogger().debug("{} = {}: {}", key, type, ignoreVersion);

                if ("regex".equals(type)) {
                    rules.merge(key, v -> v.matches(ignoreVersion), Predicate::or);
                } else if ("exact".equals(type)) {
                    rules.merge(key, v -> v.equals(ignoreVersion), Predicate::or);
                } else {
                    getLogger().warn("Unsupported ignoreVersion type: {}", type);
                }
            }
        }
    }
}
