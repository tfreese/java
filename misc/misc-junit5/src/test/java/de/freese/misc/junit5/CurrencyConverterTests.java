package de.freese.misc.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Thomas Freese
 */
@ExtendWith(MockitoExtension.class)
class CurrencyConverterTests {
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency USD = Currency.getInstance("USD");

    @Test
    void testConvertsEurToUsd(@Mock final ExchangeRateService exchangeRateService) {
        final var originalAmount = new MonetaryAmount("100.00", EUR);
        Mockito.when(exchangeRateService.getRate("EUR", "USD")).thenReturn(1.139157);

        final var currencyConverter = new CurrencyConverter(exchangeRateService);
        final var convertedAmount = currencyConverter.convert(originalAmount, USD);

        assertEquals(new BigDecimal("113.92"), convertedAmount.getValue());
        assertEquals(USD, convertedAmount.getCurrency());
    }
}
