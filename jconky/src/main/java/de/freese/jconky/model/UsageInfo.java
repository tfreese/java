// Created: 22.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public record UsageInfo(String path, long size, long used) {
    public UsageInfo() {
        this("", 0L, 0L);
    }

    public long getFree() {
        return size() - used();
    }

    /**
     * Liefert die Auslastung von 0 bis 1.<br>
     */
    public double getUsage() {
        return (double) used() / size();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "["
                + " path=" + path
                + ", size=" + size
                + ", used=" + used
                + "]";
    }
}
