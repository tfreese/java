// Created: 04.11.2018
package de.freese.sonstiges.server.handler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * The {@link IoHandler} handles the Request and Response in a separate Thread.<br>
 *
 * @author Thomas Freese
 */
public interface IoHandler<T> {
    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    void read(T input);

    void write(T output);
}
