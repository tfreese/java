// Created: 25 Juli 2024
package de.freese.sonstiges.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <a href=https://docs.oracle.com/javase/tutorial/jaxp/xslt/transformingXML.html>transformingXML</a>
 *
 * @author Thomas Freese
 */
public final class XsltDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(XsltDemo.class);

    public static void main(final String[] args) {
        Path basePath = Path.of(System.getProperty("user.dir"));

        while (basePath != null && !basePath.endsWith("java")) {
            basePath = basePath.getParent();
        }

        basePath = basePath.resolve("misc").resolve("misc-sonstiges");

        try (InputStream inputData = Files.newInputStream(basePath.resolve("src").resolve("xslt").resolve("article.xml"));
             InputStream inputStyle = Files.newInputStream(basePath.resolve("src").resolve("xslt").resolve("article.xsl"))) {

            transform(inputData, inputStyle, System.out);
        }
        catch (TransformerConfigurationException ex) {
            System.out.println("\n** Transformer Factory error");
            System.out.println("   " + ex.getMessage());

            Throwable cause = ex;

            if (ex.getException() != null) {
                cause = ex.getException();
            }

            LOGGER.error(cause.getMessage(), cause);
        }
        catch (TransformerException ex) {
            System.out.println("\n** Transformation error");
            System.out.println("   " + ex.getMessage());

            Throwable cause = ex;

            if (ex.getException() != null) {
                cause = ex.getException();
            }

            LOGGER.error(cause.getMessage(), cause);
        }
        catch (SAXException ex) {
            Exception cause = ex;

            if (ex.getException() != null) {
                cause = ex.getException();
            }

            LOGGER.error(cause.getMessage(), cause);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static void transform(final InputStream inputData, final InputStream inputStyle, final OutputStream result)
            throws TransformerException, IOException, SAXException, ParserConfigurationException {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        // dbFactory.setNamespaceAware(true);
        // dbFactory.setValidating(true);
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document document = dBuilder.parse(inputData);

        transform(new DOMSource(document), new StreamSource(inputStyle), new StreamResult(result));
    }

    private static void transform(final Source sourceData, final Source sourceStyle, final Result result) throws TransformerException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        final Transformer transformer = transformerFactory.newTransformer(sourceStyle);
        transformer.transform(sourceData, result);
    }

    private XsltDemo() {
        super();
    }
}
