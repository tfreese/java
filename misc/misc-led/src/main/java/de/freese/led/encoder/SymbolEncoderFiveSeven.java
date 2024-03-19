// Created: 20.12.23
package de.freese.led.encoder;

import java.util.HashMap;
import java.util.Map;

import de.freese.led.model.token.ArrowToken;

/**
 * {@link SymbolEncoder} for a 5 x 7 LED-Matrix.<br>
 * Every int encodes a LED-Row bitwise, see {@link SymbolEncoderMain}.
 *
 * @author Thomas Freese
 */
public class SymbolEncoderFiveSeven implements SymbolEncoder {
    private static final Map<Object, int[]> MAP;

    static {
        MAP = new HashMap<>();
        MAP.put(' ', new int[]{0, 0, 0, 0, 0, 0, 0});
        MAP.put('A', new int[]{14, 17, 17, 31, 17, 17, 17});
        MAP.put('a', new int[]{0, 0, 14, 16, 30, 17, 30});
        MAP.put('B', new int[]{15, 17, 17, 31, 17, 17, 15});
        MAP.put('b', new int[]{1, 1, 15, 17, 17, 17, 15});
        MAP.put('C', new int[]{14, 17, 1, 1, 1, 17, 14});
        MAP.put('c', new int[]{0, 0, 30, 1, 1, 1, 30});
        MAP.put('D', new int[]{15, 18, 18, 18, 18, 18, 15});
        MAP.put('d', new int[]{16, 16, 22, 25, 17, 17, 30});
        MAP.put('E', new int[]{31, 1, 1, 7, 1, 1, 31});
        MAP.put('e', new int[]{0, 0, 14, 17, 31, 1, 14});
        MAP.put('F', new int[]{31, 1, 1, 7, 1, 1, 1});
        MAP.put('f', new int[]{12, 18, 2, 7, 2, 2, 2});
        MAP.put('G', new int[]{14, 17, 1, 25, 17, 17, 14});
        MAP.put('g', new int[]{0, 0, 30, 17, 30, 16, 15});
        MAP.put('H', new int[]{17, 17, 17, 31, 17, 17, 17});
        MAP.put('h', new int[]{1, 1, 1, 13, 19, 17, 17});
        MAP.put('I', new int[]{14, 4, 4, 4, 4, 4, 14});
        MAP.put('i', new int[]{4, 0, 6, 4, 4, 4, 14});
        MAP.put('J', new int[]{28, 8, 8, 8, 8, 9, 6});
        MAP.put('j', new int[]{8, 0, 12, 8, 8, 9, 6});
        MAP.put('K', new int[]{17, 9, 5, 3, 5, 9, 17});
        MAP.put('k', new int[]{1, 1, 9, 5, 3, 5, 9});
        MAP.put('L', new int[]{1, 1, 1, 1, 1, 1, 31});
        MAP.put('l', new int[]{6, 4, 4, 4, 4, 4, 14});
        MAP.put('M', new int[]{17, 27, 21, 17, 17, 17, 17});
        MAP.put('m', new int[]{0, 0, 11, 21, 21, 17, 17});
        MAP.put('N', new int[]{17, 17, 19, 21, 25, 17, 17});
        MAP.put('n', new int[]{0, 0, 13, 19, 17, 17, 17});
        MAP.put('O', new int[]{14, 17, 17, 17, 17, 17, 14});
        MAP.put('o', new int[]{0, 0, 14, 17, 17, 17, 14});
        MAP.put('P', new int[]{15, 17, 17, 15, 1, 1, 1});
        MAP.put('p', new int[]{0, 0, 15, 17, 15, 1, 1});
        MAP.put('Q', new int[]{14, 17, 17, 17, 21, 9, 22});
        MAP.put('q', new int[]{0, 0, 30, 17, 30, 16, 16});
        MAP.put('R', new int[]{15, 17, 17, 15, 5, 9, 17});
        MAP.put('r', new int[]{0, 0, 13, 19, 1, 1, 1});
        MAP.put('S', new int[]{14, 17, 1, 14, 16, 17, 14});
        MAP.put('s', new int[]{0, 0, 14, 1, 14, 16, 15});
        MAP.put('T', new int[]{31, 4, 4, 4, 4, 4, 4});
        MAP.put('t', new int[]{2, 2, 7, 2, 2, 2, 28});
        MAP.put('U', new int[]{17, 17, 17, 17, 17, 17, 14});
        MAP.put('u', new int[]{0, 0, 17, 17, 17, 25, 22});
        MAP.put('V', new int[]{17, 17, 17, 10, 10, 4, 4});
        MAP.put('v', new int[]{0, 0, 17, 17, 17, 10, 4});
        MAP.put('W', new int[]{17, 17, 17, 21, 21, 27, 17});
        MAP.put('w', new int[]{0, 0, 17, 17, 21, 21, 10});
        MAP.put('X', new int[]{17, 17, 10, 4, 10, 17, 17});
        MAP.put('x', new int[]{0, 0, 17, 10, 4, 10, 17});
        MAP.put('Y', new int[]{17, 17, 17, 10, 4, 4, 4});
        MAP.put('y', new int[]{17, 17, 17, 17, 30, 16, 14});
        MAP.put('Z', new int[]{31, 16, 8, 4, 2, 1, 31});
        MAP.put('z', new int[]{0, 0, 31, 8, 4, 2, 31});

        MAP.put('0', new int[]{14, 17, 17, 17, 17, 17, 14});
        MAP.put('1', new int[]{4, 6, 4, 4, 4, 4, 14});
        MAP.put('2', new int[]{14, 17, 16, 8, 6, 1, 31});
        MAP.put('3', new int[]{14, 17, 16, 12, 16, 17, 14});
        MAP.put('4', new int[]{8, 12, 10, 9, 31, 8, 8});
        MAP.put('5', new int[]{31, 1, 15, 16, 16, 17, 14});
        MAP.put('6', new int[]{28, 2, 1, 15, 17, 17, 14});
        MAP.put('7', new int[]{31, 16, 8, 4, 2, 2, 2});
        MAP.put('8', new int[]{14, 17, 17, 14, 17, 17, 14});
        MAP.put('9', new int[]{14, 17, 17, 30, 16, 8, 7});

        MAP.put('^', new int[]{4, 10, 17, 0, 0, 0, 0});
        MAP.put('!', new int[]{4, 4, 4, 4, 4, 0, 4});
        MAP.put('"', new int[]{10, 10, 0, 0, 0, 0, 0});
        MAP.put('$', new int[]{4, 30, 5, 31, 20, 15, 4});
        MAP.put('%', new int[]{0, 19, 11, 4, 26, 25, 0});
        MAP.put('&', new int[]{2, 5, 5, 2, 21, 9, 22});
        MAP.put('/', new int[]{0, 16, 8, 4, 2, 1, 0});
        MAP.put('(', new int[]{4, 2, 2, 2, 2, 2, 4});
        MAP.put(')', new int[]{4, 8, 8, 8, 8, 8, 4});
        MAP.put('=', new int[]{0, 0, 31, 0, 31, 0, 0});
        MAP.put('?', new int[]{14, 17, 8, 4, 4, 0, 4});

        MAP.put('°', new int[]{4, 10, 4, 0, 0, 0, 0});
        MAP.put('[', new int[]{14, 2, 2, 2, 2, 2, 14});
        MAP.put(']', new int[]{14, 8, 8, 8, 8, 8, 14});
        MAP.put('{', new int[]{12, 2, 2, 1, 2, 2, 12});
        MAP.put('}', new int[]{6, 8, 8, 16, 8, 8, 6});

        MAP.put('ß', new int[]{14, 17, 17, 13, 17, 17, 13});
        MAP.put('\\', new int[]{0, 1, 2, 4, 8, 16, 0});

        MAP.put('+', new int[]{4, 4, 4, 31, 4, 4, 4});
        MAP.put('*', new int[]{4, 21, 14, 31, 14, 21, 4});
        MAP.put('~', new int[]{0, 0, 2, 21, 8, 0, 0});

        MAP.put('#', new int[]{10, 10, 31, 10, 31, 10, 10});
        MAP.put('\'', new int[]{4, 4, 0, 0, 0, 0, 0});

        MAP.put(',', new int[]{0, 0, 0, 12, 12, 8, 4});
        MAP.put(';', new int[]{12, 12, 0, 12, 12, 8, 4});

        MAP.put('.', new int[]{0, 0, 0, 0, 0, 12, 12});
        MAP.put(':', new int[]{0, 12, 12, 0, 12, 12, 0});

        MAP.put('-', new int[]{0, 0, 0, 31, 0, 0, 0});
        MAP.put('_', new int[]{0, 0, 0, 0, 0, 0, 31});

        MAP.put('@', new int[]{14, 17, 29, 21, 13, 1, 14});
        MAP.put('€', new int[]{14, 17, 7, 1, 7, 17, 14});

        MAP.put('<', new int[]{8, 4, 2, 1, 2, 4, 8});
        MAP.put('>', new int[]{2, 4, 8, 16, 8, 4, 2});
        MAP.put('|', new int[]{4, 4, 4, 4, 4, 4, 4});

        MAP.put('µ', new int[]{0, 9, 9, 9, 7, 1, 1});

        MAP.put('Ä', new int[]{10, 0, 14, 17, 17, 31, 17});
        MAP.put('ä', new int[]{10, 0, 14, 16, 30, 17, 30});
        MAP.put('Ü', new int[]{17, 0, 17, 17, 17, 17, 14});
        MAP.put('ü', new int[]{0, 0, 17, 0, 17, 17, 14});
        MAP.put('Ö', new int[]{10, 0, 14, 17, 17, 17, 14});
        MAP.put('ö', new int[]{0, 10, 0, 14, 17, 17, 14});

        MAP.put(ArrowToken.Arrow.INCREASING, new int[]{0, 0, 4, 14, 31, 0, 0});
        MAP.put(ArrowToken.Arrow.DECREASING, new int[]{0, 0, 0, 31, 14, 4, 0});
        MAP.put(ArrowToken.Arrow.LEFT, new int[]{0, 8, 12, 14, 12, 8, 0});
        MAP.put(ArrowToken.Arrow.RIGHT, new int[]{0, 2, 6, 14, 6, 2, 0});
        MAP.put(ArrowToken.Arrow.UNCHANGED, new int[]{0, 0, 14, 31, 14, 0, 0});
    }

    @Override
    public int[] getEncoded(final Object symbol) {
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
