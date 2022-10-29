package de.freese.misc.junit5;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * The type Calculator.
 *
 * @author Thomas Freese
 */
public class Calculator
{
    /**
     *
     */
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    /**
     *
     */
    private BigDecimal value;

    /**
     * Erstellt ein neues {@link Calculator} Object.
     */
    public Calculator()
    {
        this(0);
    }

    /**
     * Erstellt ein neues {@link Calculator} Object.
     *
     * @param value long
     */
    public Calculator(final long value)
    {
        set(BigDecimal.valueOf(value));
    }

    /**
     * Add calculator.
     *
     * @param addend long
     *
     * @return {@link Calculator}
     */
    public Calculator add(final long addend)
    {
        return set(this.value.add(BigDecimal.valueOf(addend)));
    }

    /**
     * Divide calculator.
     *
     * @param divisor long
     *
     * @return {@link Calculator}
     */
    public Calculator divide(final long divisor)
    {
        return set(this.value.divide(BigDecimal.valueOf(divisor), MATH_CONTEXT));
    }

    /**
     * Double value double.
     *
     * @return double
     */
    public double doubleValue()
    {
        return this.value.doubleValue();
    }

    /**
     * Get big decimal.
     *
     * @return {@link BigDecimal}
     */
    public BigDecimal get()
    {
        return this.value;
    }

    /**
     * Long value.
     *
     * @return long
     */
    public long longValue()
    {
        return this.value.longValue();
    }

    /**
     * Multiply calculator.
     *
     * @param factor long
     *
     * @return {@link Calculator}
     */
    public Calculator multiply(final long factor)
    {
        return set(this.value.multiply(BigDecimal.valueOf(factor)));
    }

    /**
     * Power calculator.
     *
     * @param exponent int
     *
     * @return {@link Calculator}
     */
    public Calculator power(final int exponent)
    {
        return set(this.value.pow(exponent, MATH_CONTEXT));
    }

    /**
     * Set calculator.
     *
     * @param value {@link java.math.BigDecimal}
     *
     * @return {@link Calculator}
     */
    public Calculator set(final BigDecimal value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("cannot set value to null");
        }

        this.value = value;
        return this;
    }

    /**
     * Set calculator.
     *
     * @param value double
     *
     * @return {@link Calculator}
     */
    public Calculator set(final double value)
    {
        return set(BigDecimal.valueOf(value));
    }

    /**
     * Set calculator.
     *
     * @param value long
     *
     * @return {@link Calculator}
     */
    public Calculator set(final long value)
    {
        return set(BigDecimal.valueOf(value));
    }

    /**
     * Sqrt calculator.
     *
     * @return {@link Calculator}
     */
    public Calculator sqrt()
    {
        return set(this.value.sqrt(MATH_CONTEXT));
    }
}
