// Created: 20.12.23
package de.freese.led.encoder;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface SymbolEncoder {
    int[] getEncoded(char symbol);
}
