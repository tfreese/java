// Created: 29.06.2003
package de.freese.sonstiges.print;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * Klasse für das Deckblatt - die einfachste Printable-Klasse
 *
 * @author Thomas Freese
 */
class CoverPage implements Printable {
    /**
     * Ausgabe auf dem Drucker machen.<br>
     * Die PageIndex-Abfrage entfällt, da im "book" fest eine Seite eingestellt ist.
     *
     * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
     */
    @Override
    public int print(final Graphics g, final PageFormat pageFormat, final int iPageIndex) throws PrinterException {
        // Schriftfarbe einstellen
        g.setColor(Color.black);

        // Text mit Schriftgrösse 30 ausgeben
        g.setFont(g.getFont().deriveFont(128F));
        g.drawString("JAVA!", 100, 300);

        return Printable.PAGE_EXISTS;
    }
}
