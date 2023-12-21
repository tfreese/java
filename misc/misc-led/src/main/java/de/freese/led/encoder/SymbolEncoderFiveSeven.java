// Created: 20.12.23
package de.freese.led.encoder;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link SymbolEncoder} for a 5 x 7 LED-Matrix.<br>
 * Every int encodes a LED-Row bitwise, see {@link SymbolEncoderMain}.
 *
 * @author Thomas Freese
 */
public class SymbolEncoderFiveSeven implements SymbolEncoder {
    private static final Map<Character, int[]> MAP;

    static {
        MAP = new HashMap<>();
        MAP.put(' ', new int[]{0, 0, 0, 0, 0, 0, 0});
        MAP.put('A', new int[]{14, 17, 17, 31, 17, 17, 17});
        MAP.put('a', new int[]{0, 0, 14, 16, 30, 17, 30});
        MAP.put('B', new int[]{15, 17, 17, 31, 17, 17, 15});
        MAP.put('b', new int[]{1, 1, 15, 17, 17, 17, 15});

        MAP.put('?', new int[]{14, 17, 8, 4, 4, 0, 4});
    }

    @Override
    public int[] getEncoded(final char symbol) {
        return MAP.getOrDefault(symbol, getDefault());
    }

    @Override
    public int getHorizontalDots() {
        return 5;
    }

    @Override
    public int getVerticalDots() {
        return 7;
    }

    protected int[] getDefault() {
        return MAP.get('?');
    }
}
