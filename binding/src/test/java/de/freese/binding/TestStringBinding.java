// Created: 31.07.2018
package de.freese.binding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.freese.binding.binds.BooleanBinding;
import de.freese.binding.binds.IntegerBinding;
import de.freese.binding.binds.StringBinding;
import de.freese.binding.property.Property;
import de.freese.binding.property.SimpleStringProperty;
import de.freese.binding.value.ChangeListener;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestStringBinding {
    @Test
    void testBindingConcat() {
        final SimpleStringProperty p1 = new SimpleStringProperty();
        final SimpleStringProperty p2 = new SimpleStringProperty();

        final StringBinding binding = p1.concat(p2);
        assertNull(binding.getValue());

        p1.setValue("a");
        p2.setValue("");
        assertEquals("a", binding.getValue());

        p2.setValue("-b");
        assertEquals("a-b", binding.getValue());
    }

    @Test
    void testBindingIsEmpty() {
        final SimpleStringProperty p1 = new SimpleStringProperty();

        final BooleanBinding bindingEmpty = p1.isEmpty();
        final BooleanBinding bindingNotEmpty = p1.isNotEmpty();

        assertTrue(bindingEmpty.get());
        assertFalse(bindingNotEmpty.get());

        p1.setValue("a");
        assertFalse(bindingEmpty.get());
        assertTrue(bindingNotEmpty.get());
    }

    @Test
    void testBindingLength() {
        final SimpleStringProperty p1 = new SimpleStringProperty();

        final IntegerBinding binding = p1.length();
        assertEquals(0, binding.get());

        p1.setValue("a");
        assertEquals(1, binding.get());

        p1.setValue("a a ");
        assertEquals(4, binding.get());
    }

    @Test
    void testProperty() {
        final Property<String> p = new SimpleStringProperty();

        ChangeListener<String> listener = (observable, oldValue, newValue) -> assertEquals("TeSt", newValue);
        p.addListener(listener);
        p.setValue("TeSt");
        p.removeListener(listener);

        listener = (observable, oldValue, newValue) -> {
            assertEquals("TeSt", oldValue);
            assertNull(newValue);
        };
        p.addListener(listener);
        p.setValue(null);
        p.removeListener(listener);
    }
}
