// Created: 06.11.2013
package de.freese.sonstiges.mockito;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

/**
 * @author Thomas Freese
 */
class TestVendingMaschine {
    @Test
    void testWithMockito() throws Exception {
        final CashBox cashBox = mock(CashBox.class);
        when(cashBox.getCurrentAmount()).thenReturn(42);

        doThrow(new IllegalArgumentException("Invalid value")).when(cashBox).withdraw(ArgumentMatchers.intThat(new NonNegativeIntegerMatcher()));

        final Box box = mock(Box.class);
        when(box.isEmpty()).thenReturn(Boolean.FALSE);
        when(box.getPrice()).thenReturn(42);

        final Box[] boxes = {box};
        final VendingMaschine maschine = new VendingMaschine(cashBox, boxes);
        maschine.selectItem(0);

        // Sicherstellen, dass Methoden mit diesen Parametern einmal aufgerufen wurden.
        verify(cashBox).withdraw(42);
        verify(box, times(1)).releaseItem();
    }
}
