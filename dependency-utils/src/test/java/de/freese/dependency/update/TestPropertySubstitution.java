package de.freese.dependency.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.freese.dependency.utils.PropertySubstitution;

/**
 * @author Thomas Freese
 */
class TestPropertySubstitution {
    @Test
    void testPropertySubstitution() {
        final Map<String, String> map = new LinkedHashMap<>();
        map.put("d", "dValue + ${c} + ${b} + ${a}");
        map.put("c", "cValue + ${b} + ${a}");
        map.put("b", "bValue + ${a}");
        map.put("a", "aValue");
        map.put("e", "${?}");

        final Map<String, String> unresolvable = PropertySubstitution.replacePlaceHolder(map);
        assertNotNull(unresolvable);
        assertEquals(1, unresolvable.size());
        assertEquals("${?}", unresolvable.get("e"));
        assertEquals("${?}", map.get("e"));
        assertEquals("dValue + cValue + bValue + aValue + aValue + bValue + aValue + aValue", map.get("d"));
        assertEquals("cValue + bValue + aValue + aValue", map.get("c"));
        assertEquals("bValue + aValue", map.get("b"));
        assertEquals("aValue", map.get("a"));
    }
}
