package de.freese.sonstiges.disruptor.example;

/**
 * @author Thomas Freese
 * @since 26.08.2020
 */
public class LongEvent {
    private long value;

    public void clear() {
        value = 0;
    }

    public void setValue(final long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
                + "value=" + value
                + ']';
    }
}
