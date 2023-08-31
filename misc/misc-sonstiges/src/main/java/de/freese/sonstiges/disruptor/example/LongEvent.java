// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

/**
 * @author Thomas Freese
 */
public class LongEvent {
    private long value;

    public void clear() {
        this.value = 0;
    }

    public void setValue(final long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" [");
        sb.append("value=").append(this.value);
        sb.append(']');

        return sb.toString();
    }
}
