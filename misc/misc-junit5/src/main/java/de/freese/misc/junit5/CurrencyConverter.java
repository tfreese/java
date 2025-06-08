package de.freese.misc.junit5;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * The type Currency converter.
 *
 * @author Thomas Freese
 */
public class CurrencyConverter {
    private final ExchangeRateService exchangeRateService;

    public CurrencyConverter(final ExchangeRateService exchangeRateService) {
        super();

        this.exchangeRateService = Objects.requireNonNull(exchangeRateService, "exchangeRateService required");
    }

    public MonetaryAmount convert(final MonetaryAmount amount, final Currency currency) {
        final double exchangeRate = exchangeRateService.getRate(amount.getCurrency().getCurrencyCode(), currency.getCurrencyCode());

        return new MonetaryAmount(amount.getValue().multiply(BigDecimal.valueOf(exchangeRate)), currency);
    }
}
