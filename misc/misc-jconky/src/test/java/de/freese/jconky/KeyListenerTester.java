// Created: 18 Juli 2025
package de.freese.jconky;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

/**
 * @author Thomas Freese
 */
public final class KeyListenerTester {
    public static void main(final String[] args) {
        final JFrame jFrame = new JFrame("Key Listener Tester");

        jFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent event) {
                System.out.println(event);
            }
        });

        jFrame.setSize(300, 300);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }
}
