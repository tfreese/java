package de.freese.misc.junit5;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * The type Monetary amount.
 *
 * @author Thomas Freese
 */
public class MonetaryAmount
{
    /**
     *
     */
    private final Currency currency;
    /**
     *
     */
    private final BigDecimal value;

    /**
     * Erstellt ein neues {@link MonetaryAmount} Object.
     *
     * @param value {@link BigDecimal}
     * @param currency {@link Currency}
     */
    public MonetaryAmount(final BigDecimal value, final Currency currency)
    {
        super();

        this.value = value.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    /**
     * Erstellt ein neues {@link MonetaryAmount} Object.
     *
     * @param value String
     * @param currency {@link Currency}
     */
    public MonetaryAmount(final String value, final Currency currency)
    {
        this(new BigDecimal(value), currency);
    }

    /**
     * Gets currency.
     *
     * @return {@link Currency}
     */
    public Currency getCurrency()
    {
        return this.currency;
    }

    /**
     * Gets value.
     *
     * @return {@link BigDecimal}
     */
    public BigDecimal getValue()
    {
        return this.value;
    }
}
