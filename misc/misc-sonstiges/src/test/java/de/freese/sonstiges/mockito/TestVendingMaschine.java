// Created: 06.11.2013
package de.freese.sonstiges.mockito;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * @author Thomas Freese
 */
class TestVendingMaschine {
    @Test
    void testWithMockito() throws Exception {
        final CashBox cashBox = Mockito.mock(CashBox.class);
        Mockito.when(cashBox.getCurrentAmount()).thenReturn(42);

        Mockito.doThrow(new IllegalArgumentException("Invalid value")).when(cashBox).withdraw(ArgumentMatchers.intThat(new NonNegativeIntegerMatcher()));

        final Box box = Mockito.mock(Box.class);
        Mockito.when(box.isEmpty()).thenReturn(Boolean.FALSE);
        Mockito.when(box.getPrice()).thenReturn(42);

        final Box[] boxes = {box};
        final VendingMaschine maschine = new VendingMaschine(cashBox, boxes);
        maschine.selectItem(0);

        // Sicherstellen, dass Methoden mit diesen Parametern einmal aufgerufen wurden.
        Mockito.verify(cashBox).withdraw(42);
        Mockito.verify(box, Mockito.times(1)).releaseItem();
    }
}
