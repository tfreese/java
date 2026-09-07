package de.freese.misc.junit5;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * The type Calculator.
 *
 * @author Thomas Freese
 */
public class Calculator {
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    private BigDecimal value;

    public Calculator() {
        this(0);
    }

    public Calculator(final long value) {
        super();

        set(BigDecimal.valueOf(value));
    }

    public Calculator add(final long addend) {
        return set(value.add(BigDecimal.valueOf(addend)));
    }

    public Calculator divide(final long divisor) {
        return set(value.divide(BigDecimal.valueOf(divisor), MATH_CONTEXT));
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public BigDecimal get() {
        return value;
    }

    public long longValue() {
        return value.longValue();
    }

    public Calculator multiply(final long factor) {
        return set(value.multiply(BigDecimal.valueOf(factor)));
    }

    public Calculator power(final int exponent) {
        return set(value.pow(exponent, MATH_CONTEXT));
    }

    public Calculator set(final BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("cannot set value to null");
        }

        this.value = value;
        return this;
    }

    public Calculator set(final double value) {
        return set(BigDecimal.valueOf(value));
    }

    public Calculator set(final long value) {
        return set(BigDecimal.valueOf(value));
    }

    public Calculator sqrt() {
        return set(value.sqrt(MATH_CONTEXT));
    }
}
