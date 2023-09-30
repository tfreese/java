package net.leddisplay;

/**
 * @author Thomas Freese
 */
public final class LedDisplayFactory {
    public static LedDisplay createLedDisplay() {
        return new DefaultLedDisplay();
    }

    private LedDisplayFactory() {
        super();
    }
}
