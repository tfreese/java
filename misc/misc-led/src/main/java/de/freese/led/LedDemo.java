// Created: 20.12.23
package de.freese.led;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import de.freese.led.model.token.LedToken;
import de.freese.led.model.token.TextToken;

/**
 * @author Thomas Freese
 */
public final class LedDemo {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = init();
            //        frame.pack();
            frame.setSize(2000, 400);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            frame.setVisible(true);
        });
    }

    private static JFrame init() {
        final JFrame frame = new JFrame("LED Display Demo");

        final Consumer<LedDisplay> configurer = display -> {
            display.setDotHeight(16);
            display.setDotWidth(16);
            display.sethGap(4);
            display.setvGap(4);
            //            display.setLedElement(new TextElement("AaBb ?w"));
            display.setLedElement(() -> new LedToken[]{new TextToken("AaBb ?w"), new TextToken("A", Color.RED), new TextToken("B", Color.BLUE)});
        };

        //        final String text = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789~'!@#$%^&*()-_=\\|{}[]:;,.<>?/\"";
        //        final Token textToken = new TextToken(text);
        //        final Token arrowTokenDecreasing = new ArrowToken(ArrowToken.ArrowForm.DECREASING);
        //        final Token arrowTokenIncreasing = new ArrowToken(ArrowToken.ArrowForm.INCREASING);
        //        final Token arrowTokenUnchanged = new ArrowToken(ArrowToken.ArrowForm.UNCHANGED);
        //        ledDisplay.setDisplayElement(() -> new Token[]{textToken, arrowTokenDecreasing, arrowTokenIncreasing, arrowTokenUnchanged});

        final LedDisplay ledDisplayRect = LedDisplay.withRectangles();
        configurer.accept(ledDisplayRect);

        final LedDisplay ledDisplayCircle = LedDisplay.withCircles();
        configurer.accept(ledDisplayCircle);

        final JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(ledDisplayRect.getComponent());
        panel.add(ledDisplayCircle.getComponent());

        frame.setContentPane(panel);

        return frame;
    }

    private LedDemo() {
        super();
    }
}
