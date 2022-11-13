// Created: 04.11.2018
package de.freese.sonstiges.server.handler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Verarbeitet den Request und Response.
 *
 * @param <T> Type
 *
 * @author Thomas Freese
 */
public interface IoHandler<T>
{
    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    default Charset getCharset()
    {
        return DEFAULT_CHARSET;
    }

    /**
     * Verarbeitet den Request.
     */
    void read(final T input);

    /**
     * Verarbeitet den Response.
     */
    void write(final T output);
}
