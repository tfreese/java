package de.freese.misc.junit5;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * The type Currency converter.
 *
 * @author Thomas Freese
 */
public class CurrencyConverter
{
    /**
     *
     */
    private final ExchangeRateService exchangeRateService;

    /**
     * Erstellt ein neues {@link CurrencyConverter} Object.
     *
     * @param exchangeRateService {@link ExchangeRateService}
     */
    public CurrencyConverter(final ExchangeRateService exchangeRateService)
    {
        super();

        this.exchangeRateService = Objects.requireNonNull(exchangeRateService, "exchangeRateService required");
    }

    /**
     * Convert monetary amount.
     *
     * @param amount   {@link MonetaryAmount}
     * @param currency {@link Currency}
     *
     * @return {@link MonetaryAmount}
     */
    public MonetaryAmount convert(final MonetaryAmount amount, final Currency currency)
    {
        double exchangeRate = this.exchangeRateService.getRate(amount.getCurrency().getCurrencyCode(), currency.getCurrencyCode());

        return new MonetaryAmount(amount.getValue().multiply(BigDecimal.valueOf(exchangeRate)), currency);
    }
}
