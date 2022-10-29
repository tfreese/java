// Created: 22.07.2018
package de.freese.metamodel.modelgen.naming;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * TestCase für die Namenskonvertierung.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestNaming
{
    /**
     *
     */
    @Test
    void testClassNaming()
    {
        NamingStrategy namingStrategy = new DefaultNamingStrategy();

        assertEquals("Test", namingStrategy.getClassName("Test"));
        assertEquals("Test", namingStrategy.getClassName("TEST"));
        assertEquals("Test", namingStrategy.getClassName("T_Test"));
        assertEquals("Test", namingStrategy.getClassName("T_TEST"));
        assertEquals("TestTest", namingStrategy.getClassName("Test_Test"));
        assertEquals("TestTest", namingStrategy.getClassName("TEST_TEST"));
    }

    /**
    *
    */
    @Test
    void testFieldNaming()
    {
        NamingStrategy namingStrategy = new DefaultNamingStrategy();

        assertEquals("test", namingStrategy.getFieldName("Test"));
        assertEquals("test", namingStrategy.getFieldName("TEST"));
        assertEquals("test", namingStrategy.getFieldName("T_Test"));
        assertEquals("test", namingStrategy.getFieldName("T_TEST"));
        assertEquals("testTest", namingStrategy.getFieldName("Test_Test"));
        assertEquals("testTest", namingStrategy.getFieldName("TEST_TEST"));
    }
}
