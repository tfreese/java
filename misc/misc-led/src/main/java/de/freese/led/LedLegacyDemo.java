// Created: 20.12.23
package de.freese.led;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.led.tokens.ArrowToken;
import net.led.tokens.TextToken;
import net.led.tokens.Token;
import net.leddisplay.LedDisplay;
import net.leddisplay.LedDisplayFactory;

/**
 * @author Thomas Freese
 */
public final class LedLegacyDemo {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new LedLegacyDemo().init();
            frame.setVisible(true);
        });
    }

    private LedLegacyDemo() {
        super();
    }

    private JFrame init() {
        final JFrame frame = new JFrame("LED Display Demo");
        //        frame.pack();
        frame.setSize(2000, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.setContentPane(scrollPane);

        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(2000, 200));

        final LedDisplay ledDisplay = LedDisplayFactory.createLedDisplay();
        ledDisplay.setTokenGap(2);
        ledDisplay.setDotSize(5, 5);
        ledDisplay.setDotGaps(1, 1);

        final String text = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789~'!@#$%^&*()-_=\\|{}[]:;,.<>?/\"";
        final Token textToken = new TextToken(text);
        final Token arrowTokenDecreasing = new ArrowToken(ArrowToken.ArrowForm.DECREASING);
        final Token arrowTokenIncreasing = new ArrowToken(ArrowToken.ArrowForm.INCREASING);
        final Token arrowTokenUnchanged = new ArrowToken(ArrowToken.ArrowForm.UNCHANGED);
        ledDisplay.setDisplayElement(() -> new Token[]{textToken, arrowTokenDecreasing, arrowTokenIncreasing, arrowTokenUnchanged});
        panel.add(ledDisplay.getComponent(), BorderLayout.CENTER);

        scrollPane.setViewportView(panel);

        return frame;
    }
}
