// Created: 04.11.2016
package de.freese.sonstiges;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import jakarta.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestToHex {
    private static final byte[] BYTES = new byte[1000000];
    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();
    private static final Logger LOGGER = LoggerFactory.getLogger(TestToHex.class);
    private static final String RESULT = "";

    @BeforeAll
    static void beforeAll() {
        final Random random = new Random(System.currentTimeMillis());

        random.nextBytes(BYTES);
    }

    @Test
    void testBitShift() {
        final long start = System.currentTimeMillis();
        final StringBuilder sb = new StringBuilder(BYTES.length * 2);

        for (byte element : BYTES) {
            final int byteValue = element & 0xFF;

            sb.append(HEX_CODE[(byteValue >> 4) & 0xF]);
            sb.append(HEX_CODE[byteValue & 0xF]);
        }

        // LOGGER.info(sb);
        LOGGER.info("testBitShift: {} ms", System.currentTimeMillis() - start);

        assertTrue(true);
    }

    @Test
    void testDatatypeConverter() {
        final long start = System.currentTimeMillis();
        final StringBuilder sb = new StringBuilder(BYTES.length * 2);
        sb.append(DatatypeConverter.printHexBinary(BYTES));

        // LOGGER.info(sb);
        LOGGER.info("testDatatypeConverter: {} ms", System.currentTimeMillis() - start);

        assertTrue(true);
    }

    @Test
    void testIntegerToHexString() {
        final long start = System.currentTimeMillis();
        final StringBuilder sb = new StringBuilder(BYTES.length * 2);

        for (byte element : BYTES) {
            final String hex = Integer.toHexString(element).toUpperCase();

            if (hex.length() == 1) {
                sb.append("0");
            }

            sb.append(hex);
        }

        // LOGGER.info(sb);
        LOGGER.info("testIntegerToHexString: {} ms", System.currentTimeMillis() - start);

        assertTrue(true);
    }

    @Test
    void testIntegerToString() {
        final long start = System.currentTimeMillis();
        final StringBuilder sb = new StringBuilder(BYTES.length * 2);

        for (byte element : BYTES) {
            sb.append(Integer.toString((element & 0xFF) + 0x100, 16).substring(1).toUpperCase());
        }

        // LOGGER.info(sb);
        LOGGER.info("testIntegerToString: {} ms", System.currentTimeMillis() - start);

        assertTrue(true);
    }

    @Test
    void testStringFormat() {
        final long start = System.currentTimeMillis();
        final StringBuilder sb = new StringBuilder(BYTES.length * 2);

        for (byte element : BYTES) {
            sb.append(String.format("%02X", element));
        }

        // LOGGER.info(sb);
        LOGGER.info("testStringFormat: {} ms", System.currentTimeMillis() - start);

        assertTrue(true);
    }
}
