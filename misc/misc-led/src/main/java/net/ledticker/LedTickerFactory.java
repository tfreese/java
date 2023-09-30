package net.ledticker;

/**
 * @author Thomas Freese
 */
public final class LedTickerFactory {
    public static LedTicker createLedTicker() {
        return new DefaultLedTicker();
    }

    private LedTickerFactory() {
        super();
    }
}
