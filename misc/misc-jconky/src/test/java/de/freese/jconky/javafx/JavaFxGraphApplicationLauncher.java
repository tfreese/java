// Created: 13.11.22
package de.freese.jconky.javafx;

import javafx.application.Application;

/**
 * @author Thomas Freese
 */
public final class JavaFxGraphApplicationLauncher {
    public static void main(final String[] args) {
        Application.launch(JavaFxGraphApplication.class, args);
    }

    private JavaFxGraphApplicationLauncher() {
        super();
    }
}
