// Created: 22.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class UsageInfo
{
    /**
     *
     */
    private final String path;
    /**
     *
     */
    private final long size;
    /**
     *
     */
    private final long used;

    /**
     * Erstellt ein neues {@link UsageInfo} Object.
     */
    public UsageInfo()
    {
        this("", 0L, 0L);
    }

    /**
     * Erstellt ein neues {@link UsageInfo} Object.
     *
     * @param path String
     * @param size long
     * @param used long
     */
    public UsageInfo(final String path, final long size, final long used)
    {
        super();

        this.path = path;
        this.size = size;
        this.used = used;
    }

    /**
     * @return long
     */
    public long getFree()
    {
        return getSize() - getUsed();
    }

    /**
     * @return String
     */
    public String getPath()
    {
        return this.path;
    }

    /**
     * @return long
     */
    public long getSize()
    {
        return this.size;
    }

    /**
     * Liefert die Auslastung von 0 bis 1.<br>
     *
     * @return double
     */
    public double getUsage()
    {
        return (double) getUsed() / getSize();
    }

    /**
     * @return long
     */
    public long getUsed()
    {
        return this.used;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append(" path=").append(this.path);
        builder.append(", size=").append(this.size);
        builder.append(", used=").append(this.used);
        builder.append("]");

        return builder.toString();
    }
}
