// Created: 30.11.2020
package de.freese.jconky.model;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * @param <T> Entity Type
 *
 * @author Thomas Freese
 */
public final class Values<T extends Comparable<?>> {
    private final TreeSet<T> treeSet = new TreeSet<>();

    private final LinkedList<T> valueList = new LinkedList<>();

    private LinkedList<T> newValues;

    public synchronized void addValue(final T value) {
        if (this.newValues == null) {
            this.newValues = new LinkedList<>();
        }

        this.newValues.add(value);
    }

    /**
     * Liefert die letzten n Werte.<br>
     */
    public synchronized List<T> getLastValues(final int count) {
        final List<T> lastValues = this.newValues;
        this.newValues = null;

        if (lastValues != null) {
            // Neue Werte hinzufügen.
            this.valueList.addAll(lastValues);
        }

        // Alte Werte entfernen.
        int n = Math.min(count, this.valueList.size());

        while (this.valueList.size() > n) {
            T oldValue = this.valueList.removeFirst();

            this.treeSet.remove(oldValue);
        }

        if (lastValues != null) {
            // Neue Werte für min.-/max. hinzufügen.
            this.treeSet.addAll(lastValues);
        }

        return this.valueList;
    }

    public T getMaxValue() {
        return this.treeSet.last();
    }

    public T getMinValue() {
        return this.treeSet.first();
    }
}
