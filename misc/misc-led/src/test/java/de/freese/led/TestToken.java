// Created: 20.12.23
package de.freese.led;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.freese.led.model.token.NumberToken;

/**
 * @author Thomas Freese
 */
class TestToken {
    @Test
    void numberToken() {
        final NumberToken token = new NumberToken();
        assertEquals("N/A", token.getValue());

        token.setValue(Double.NaN);
        assertEquals("N/A", token.getValue());

        token.setValue(Double.NEGATIVE_INFINITY);
        assertEquals("N/A", token.getValue());

        token.setValue(Double.POSITIVE_INFINITY);
        assertEquals("N/A", token.getValue());

        token.setValue(null);
        assertEquals("N/A", token.getValue());

        token.setValue(1);
        assertEquals("1", token.getValue());

        token.setValue(1L);
        assertEquals("1", token.getValue());

        token.setValue(1.1F);
        assertEquals("1.1", token.getValue());

        token.setValue(1.1D);
        assertEquals("1.1", token.getValue());

        token.setValue(1.1111D);
        assertEquals("1.111", token.getValue());
    }
}
