package de.freese.misc.junit5;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * The type Monetary amount.
 *
 * @author Thomas Freese
 */
public class MonetaryAmount {
    private final Currency currency;
    private final BigDecimal value;

    public MonetaryAmount(final BigDecimal value, final Currency currency) {
        super();

        this.value = value.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public MonetaryAmount(final String value, final Currency currency) {
        this(new BigDecimal(value), currency);
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getValue() {
        return value;
    }
}
