package de.freese.led.encoder;

/**
 * Every int encodes a LED-Row bitwise, see {@link SymbolEncoderMain}.
 *
 * @author Thomas Freese
 * @since 20.12.23
 */
public interface SymbolEncoder {
    int[] getEncoded(Object symbol);

    int getHorizontalDots();

    int getVerticalDots();
}
