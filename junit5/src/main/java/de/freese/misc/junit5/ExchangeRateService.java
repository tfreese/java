package de.freese.misc.junit5;

/**
 * The interface Exchange rate service.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ExchangeRateService {
    double getRate(String sourceCurrency, String targetCurrency);
}
