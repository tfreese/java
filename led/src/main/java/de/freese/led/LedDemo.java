package de.freese.led;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

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

            // LED-Panel initialisieren: 24 LEDs hoch, Schriftgröße 20.
            final String text = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789^!\"$%&/()=°[]{}ß?\\+*~#',;.:-_@€<>|µÄäÜüÖö";

            final LedPanel ledPanel = new LedPanel(text, 24, 80);

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
