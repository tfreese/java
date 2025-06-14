// Created: 22.09.2016
package de.freese.sonstiges.imap.analyze;

import java.io.Reader;
import java.io.StringReader;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Extrahiert aus einem HTML-Dokument die Tags und liefert den reinen Text.
 *
 * @author Thomas Freese
 */
public class Html2Text extends HTMLEditorKit.ParserCallback {

    private StringBuilder sb;

    public String getText() {
        return sb.toString();
    }

    @Override
    public void handleText(final char[] data, final int pos) {
        sb.append(data).append(" ");
    }

    public Html2Text parse(final String html) throws Exception {
        sb = new StringBuilder();

        final ParserDelegator delegator = new ParserDelegator();

        // org.apache.lucene.analysis.charfilter.HTMLStripCharFilter
        try (Reader reader = new StringReader(html)) {
            delegator.parse(reader, this, Boolean.TRUE);
        }

        return this;
    }
}
