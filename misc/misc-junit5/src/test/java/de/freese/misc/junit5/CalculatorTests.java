package de.freese.misc.junit5;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * @author Thomas Freese
 */
class CalculatorTests {
    private Calculator calculator;

    @BeforeEach
    void createCalculator() {
        this.calculator = new Calculator();
    }

    // @Disabled
    @Test
    @Tag("input-validation")
    void testCannotSetValueToNull() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> this.calculator.set(null));

        Assertions.assertEquals("cannot set value to null", exception.getMessage());
    }

    @Test
    @DisplayName("(2 * 3) / 4 = 6/4 = 3/2 = 1.5")
    @Tag("multiplication")
    @Tag("division")
    void testDivideResultOfMultiplication() {
        BigDecimal newValue = this.calculator.set(2).multiply(3).divide(4).get();

        Assertions.assertEquals(new BigDecimal("1.5"), newValue);
    }

    /**
     * Dieser Test ist durch RNG nicht immer erfolgreich.
     */
    @Disabled("Dieser Test ist durch RNG nicht immer erfolgreich.")
    @RepeatedTest(10)
    @Tag("power")
    void testFlakyTest() {
        double actualResult = this.calculator.set(Math.random()).power(2).doubleValue();

        Assertions.assertEquals(0.0, actualResult, 0.5);
    }

    @Test
    @DisplayName("1 + 1 = 2")
    @Tag("addition")
    void testOnePlusOneIsTwo() {
        long newValue = this.calculator.set(1).add(1).longValue();

        Assertions.assertEquals(2, newValue);
    }

    @TestFactory
    @Tag("multiplication")
    @Tag("power")
    Stream<DynamicTest> testPowerOfTwo() {
        return IntStream.range(1, 100).mapToObj(value -> DynamicTest.dynamicTest(MessageFormat.format("{0}^2 = {0} * {0}", value), () -> {
            var expectedValue = new Calculator(value).multiply(value).get();
            var actualValue = this.calculator.set(value).power(2).get();
            Assertions.assertEquals(expectedValue, actualValue);
        }));
    }

    @ParameterizedTest(name = "sqrt({0}) = {1}")
    @CsvSource({
            // @formatter:off
            "1, 1.0000000000000000",
            "2, 1.4142135623730951",
            "3, 1.7320508075688772",
            "4, 2.0000000000000000"
            // @formatter:on
    })
    @Tag("sqrt")
    void testSqrt(final long input, final double expectedResult) {
        double actualResult = this.calculator.set(input).sqrt().doubleValue();

        Assertions.assertEquals(expectedResult, actualResult, 1e-16);
    }

    @ParameterizedTest(name = "sqrt({0}) = {1}")
    @CsvFileSource(resources = "/sqrt.csv")
    @Tag("sqrt")
    void testSqrtFromFile(final long input, final double expectedResult) {
        double actualResult = this.calculator.set(input).sqrt().doubleValue();

        Assertions.assertEquals(expectedResult, actualResult, 1e-16);
    }
}
