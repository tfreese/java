// Created: 18.01.2021
package de.freese.sonstiges.javafx;

import javafx.application.Application;

/**
 * @author Thomas Freese
 */
public final class JavaFxApplicationLauncher
{
    public static void main(final String[] args)
    {
        Application.launch(JavaFxApplication.class, args);
    }

    private JavaFxApplicationLauncher()
    {
        super();
    }
}
