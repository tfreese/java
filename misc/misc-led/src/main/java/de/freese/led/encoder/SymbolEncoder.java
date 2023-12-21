// Created: 20.12.23
package de.freese.led.encoder;

/**
 * Every int encodes a LED-Row bitwise, see {@link SymbolEncoderMain}.
 *
 * @author Thomas Freese
 */
public interface SymbolEncoder {
    int[] getEncoded(char symbol);

    int getHorizontalDots();

    int getVerticalDots();
}
