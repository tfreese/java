package de.freese.misc.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class SharedResourcesTests {
    private Properties backup;

    @BeforeEach
    void backup() {
        backup = new Properties();
        backup.putAll(System.getProperties());
    }

    @AfterEach
    void restore() {
        System.setProperties(backup);
    }

    @Test
    @ResourceLock(value = "system.properties", mode = ResourceAccessMode.READ_WRITE)
    void testCanSetCustomPropertyToBar() {
        System.setProperty("my.prop", "bar");
        assertEquals("bar", System.getProperty("my.prop"));
    }

    @Test
    @ResourceLock(value = "system.properties", mode = ResourceAccessMode.READ_WRITE)
    void testCanSetCustomPropertyToFoo() {
        System.setProperty("my.prop", "foo");
        assertEquals("foo", System.getProperty("my.prop"));
    }

    @Test
    @ResourceLock(value = "system.properties", mode = ResourceAccessMode.READ)
    void testCustomPropertyIsNotSetByDefault() {
        Assertions.assertNull(System.getProperty("my.prop"));
    }
}
