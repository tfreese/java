package de.freese.led;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import de.freese.led.model.LedModel;
import de.freese.led.model.LedSymbol;

/**
 * @author Thomas Freese
 * @since 13.09.26
 */
public final class LedDemo {
    static void main() {
        // Swing-UI auf dem Event Dispatch Thread (EDT) starten
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame("Java Swing LED Lauftext Demo");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(Color.BLACK);

            final String text = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789^!\"$%&/()=°[]{}ß?\\+*~#',;.:-_@€<>|µÄäÜüÖö";

            // Den Text in eine simple logische Boolean-Matrix umwandeln (true = LED an).
            // 24 LEDs hoch, 80 LEDs breit, Schriftgröße 20.
            final LedModel ledModel = new LedModel(24, 80, new Font(Font.MONOSPACED, Font.PLAIN, 20))
                    .addSymbol(LedSymbol.ARROW_UP, Color.GREEN)
                    .addSymbol(LedSymbol.ARROW_DOWN, Color.RED)
                    .addText(text);

            final LedPanel ledPanel = new LedPanel(ledModel);

            frame.add(ledPanel, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Animation starten
            ledPanel.startAnimation();
        });
    }

    private LedDemo() {
        super();
    }
}
