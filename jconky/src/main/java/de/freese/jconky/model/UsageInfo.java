// Created: 22.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public record UsageInfo(String path, long total, long used, long free) {
    public UsageInfo() {
        this("", 0L, 0L, 0L);
    }

    /**
     * Liefert die Auslastung von 0 bis 1.<br>
     */
    public double getUsage() {
        return (double) used() / total();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "["
                + " path=" + path
                + ", total=" + total
                + ", used=" + used
                + ", free=" + free
                + "]";
    }
}
