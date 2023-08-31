// Created: 04.11.2018
package de.freese.sonstiges.server.handler;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verarbeitet den Request und Response.<br>
 * Basis-Implementierung des {@link IoHandler}.
 *
 * @author Thomas Freese
 * @see IoHandler
 */
public abstract class AbstractIoHandler<T> implements IoHandler<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected CharsetDecoder getCharsetDecoder() {
        return DEFAULT_CHARSET.newDecoder();
    }

    protected CharsetEncoder getCharsetEncoder() {
        return DEFAULT_CHARSET.newEncoder();
    }

    protected Logger getLogger() {
        return this.logger;
    }
}
