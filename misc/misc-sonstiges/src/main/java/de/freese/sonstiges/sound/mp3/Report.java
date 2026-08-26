package de.freese.sonstiges.sound.mp3;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Thomas Freese
 * @since 28.09.2013
 */
final class Report implements Comparable<Report> {
    private final File file;
    private final Set<String> messages = new TreeSet<>();

    Report(final File file) {
        super();

        this.file = file;
    }

    public void addMessage(final String text) {
        messages.add(text);
    }

    @Override
    public int compareTo(final Report o) {
        // if (comp == 0) {
        // comp = messages.toString().compareTo(o.messages.toString());
        // }
        return file.compareTo(o.file);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof final Report other)) {
            return false;
        }

        return Objects.equals(file, other.file) && Objects.equals(messages, other.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, messages);
    }

    @Override
    public String toString() {
        return file.getAbsolutePath() + ": " + messages;
    }

    public String toString(final Path rootDirectory) {
        return rootDirectory.relativize(file.toPath()) + ": " + messages;
    }
}
