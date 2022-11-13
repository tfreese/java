// Created: 28.09.2013
package de.freese.sonstiges.sound.mp3;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Thomas Freese
 */
final class Report implements Comparable<Report>
{
    private final File file;

    private final Set<String> messages = new TreeSet<>();

    Report(final File file)
    {
        super();

        this.file = file;
    }

    public void addMessage(final String text)
    {
        this.messages.add(text);
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Report o)
    {

        // if (comp == 0)
        // {
        // comp = this.messages.toString().compareTo(o.messages.toString());
        // }
        return this.file.compareTo(o.file);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Report other))
        {
            return false;
        }

        return Objects.equals(this.file, other.file) && Objects.equals(this.messages, other.messages);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.file, this.messages);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.file.getAbsolutePath());
        sb.append(": ");
        sb.append(this.messages);

        return sb.toString();
    }

    public String toString(final Path rootDirectory)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(rootDirectory.relativize(this.file.toPath()));
        sb.append(": ");
        sb.append(this.messages);

        return sb.toString();
    }
}
