// Created: 22.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class UsageInfo {
    private final String path;
    private final long size;
    private final long used;

    public UsageInfo() {
        this("", 0L, 0L);
    }

    public UsageInfo(final String path, final long size, final long used) {
        super();

        this.path = path;
        this.size = size;
        this.used = used;
    }

    public long getFree() {
        return getSize() - getUsed();
    }

    public String getPath() {
        return this.path;
    }

    public long getSize() {
        return this.size;
    }

    /**
     * Liefert die Auslastung von 0 bis 1.<br>
     */
    public double getUsage() {
        return (double) getUsed() / getSize();
    }

    public long getUsed() {
        return this.used;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append(" path=").append(this.path);
        builder.append(", size=").append(this.size);
        builder.append(", used=").append(this.used);
        builder.append("]");

        return builder.toString();
    }
}
