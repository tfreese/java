// Created: 20.12.23
package de.freese.led.encoder;

import java.util.HashMap;
import java.util.Map;

import de.freese.led.SymbolEncoderMain;

/**
 * {@link SymbolEncoder} for a 5 x 7 LED-Matrix.<br>
 * Every int encodes a LED-Row bitwise, see {@link SymbolEncoderMain}.
 *
 * @author Thomas Freese
 */
public class SymbolEncoderFiveSeven implements SymbolEncoder {
    private final Map<Character, int[]> map;

    public SymbolEncoderFiveSeven() {
        super();

        map = new HashMap<>();
        map.put(' ', new int[]{0, 0, 0, 0, 0, 0, 0});
        map.put('A', new int[]{14, 17, 17, 17, 31, 17, 17});
        map.put('a', new int[]{0, 0, 14, 16, 30, 17, 30});
        map.put('B', new int[]{15, 17, 17, 31, 17, 17, 15});
        map.put('b', new int[]{1, 1, 15, 17, 17, 17, 15});

        map.put('?', new int[]{14, 17, 8, 4, 4, 0, 4});
    }

    @Override
    public int[] getEncoded(final char symbol) {
        return map.getOrDefault(symbol, getDefault());
    }

    protected int[] getDefault() {
        return map.get('?');
    }
}
