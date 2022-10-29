// Created: 31.07.2018
package de.freese.binding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.freese.binding.binds.BooleanBinding;
import de.freese.binding.binds.IntegerBinding;
import de.freese.binding.binds.StringBinding;
import de.freese.binding.property.Property;
import de.freese.binding.property.SimpleStringProperty;
import de.freese.binding.value.ChangeListener;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestStringBinding
{
    /**
     *
     */
    @Test
    void testBindingConcat()
    {
        SimpleStringProperty p1 = new SimpleStringProperty();
        SimpleStringProperty p2 = new SimpleStringProperty();

        StringBinding binding = p1.concat(p2);
        assertNull(binding.getValue());

        p1.setValue("a");
        p2.setValue("");
        assertEquals("a", binding.getValue());

        p2.setValue("-b");
        assertEquals("a-b", binding.getValue());
    }

    /**
     *
     */
    @Test
    void testBindingIsEmpty()
    {
        SimpleStringProperty p1 = new SimpleStringProperty();

        BooleanBinding bindingEmpty = p1.isEmpty();
        BooleanBinding bindingNotEmpty = p1.isNotEmpty();

        assertTrue(bindingEmpty.get());
        assertFalse(bindingNotEmpty.get());

        p1.setValue("a");
        assertFalse(bindingEmpty.get());
        assertTrue(bindingNotEmpty.get());
    }

    /**
     *
     */
    @Test
    void testBindingLength()
    {
        SimpleStringProperty p1 = new SimpleStringProperty();

        IntegerBinding binding = p1.length();
        assertEquals(0, binding.get());

        p1.setValue("a");
        assertEquals(1, binding.get());

        p1.setValue("a a ");
        assertEquals(4, binding.get());
    }

    /**
     *
     */
    @Test
    void testProperty()
    {
        Property<String> p = new SimpleStringProperty();

        ChangeListener<String> listener = (observable, oldValue, newValue) -> assertEquals("TeSt", newValue);
        p.addListener(listener);
        p.setValue("TeSt");
        p.removeListener(listener);

        listener = (observable, oldValue, newValue) ->
        {
            assertEquals("TeSt", oldValue);
            assertNull(newValue);
        };
        p.addListener(listener);
        p.setValue(null);
        p.removeListener(listener);
    }
}
