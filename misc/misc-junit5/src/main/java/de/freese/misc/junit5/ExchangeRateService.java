package de.freese.misc.junit5;

/**
 * The interface Exchange rate service.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ExchangeRateService
{
    /**
     * Gets rate.
     *
     * @param sourceCurrency String
     * @param targetCurrency String
     *
     * @return double rate
     */
    double getRate(String sourceCurrency, String targetCurrency);
}
