// Created: 20.12.23
package de.freese.led;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import de.freese.led.model.element.AnimatedElement;
import de.freese.led.model.token.ArrowToken;
import de.freese.led.model.token.LedToken;
import de.freese.led.model.token.NumberToken;
import de.freese.led.model.token.TextToken;

/**
 * @author Thomas Freese
 */
public final class LedDemo {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = init();
            // frame.pack();
            frame.setSize(800, 100);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            frame.setVisible(true);
        });
    }

    private static JFrame init() {
        final JFrame frame = new JFrame("LED Display Demo");

        final Supplier<AnimatedElement> elementSupplier = () -> {
            final String text = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789^!\"$%&/()=°[]{}ß?\\+*~#',;.:-_@€<>|µÄäÜüÖö";
            final LedToken arrowTokenIncreasing = new ArrowToken(ArrowToken.Arrow.INCREASING, Color.GREEN);
            final LedToken arrowTokenDecreasing = new ArrowToken(ArrowToken.Arrow.DECREASING, Color.RED);
            final LedToken arrowTokenLeft = new ArrowToken(ArrowToken.Arrow.LEFT);
            final LedToken arrowTokenRight = new ArrowToken(ArrowToken.Arrow.RIGHT);
            final LedToken arrowTokenUnchanged = new ArrowToken(ArrowToken.Arrow.UNCHANGED);

            final AnimatedElement animatedElement = new AnimatedElement(text);

            return animatedElement
                    .addToken(arrowTokenIncreasing)
                    .addToken(arrowTokenDecreasing)
                    .addToken(arrowTokenLeft)
                    .addToken(arrowTokenRight)
                    .addToken(arrowTokenUnchanged)
                    .addToken(new NumberToken(1.9D, Color.MAGENTA))
                    .addToken(new TextToken("     "));
        };

        final Consumer<LedDisplay> configurer = display -> {
            display.setDotHeight(2);
            display.setDotWidth(2);
            display.sethGap(1);
            display.setvGap(1);
        };

        final AnimatedElement ledElementRect = elementSupplier.get();
        final LedDisplay ledDisplayRect = LedDisplay.withRectangles();
        ledDisplayRect.setLedElement(ledElementRect);
        configurer.accept(ledDisplayRect);

        final AnimatedElement ledElementCircle = elementSupplier.get();
        final LedDisplay ledDisplayCircle = LedDisplay.withCircles();
        ledDisplayCircle.setLedElement(ledElementCircle);
        configurer.accept(ledDisplayCircle);

        final JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(ledDisplayRect.getComponent());
        panel.add(ledDisplayCircle.getComponent());

        frame.setContentPane(panel);

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            ledElementRect.shiftToken();
            ledElementCircle.shiftToken();

            SwingUtilities.invokeLater(() -> {
                ledDisplayRect.update();
                ledDisplayCircle.update();
            });
        }, 1000, 100, TimeUnit.MILLISECONDS);

        return frame;
    }

    private LedDemo() {
        super();
    }
}
