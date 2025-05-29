// Created: 29.06.2003
package de.freese.sonstiges.print;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * @author Thomas Freese
 */
class CoverPage implements Printable {
    @Override
    public int print(final Graphics g, final PageFormat pageFormat, final int pageIndex) throws PrinterException {
        g.setColor(Color.black);

        g.setFont(g.getFont().deriveFont(128F));
        g.drawString("JAVA!", 100, 300);

        return Printable.PAGE_EXISTS;
    }
}
