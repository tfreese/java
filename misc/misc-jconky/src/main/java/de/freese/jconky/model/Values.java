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
        if (newValues == null) {
            newValues = new LinkedList<>();
        }

        newValues.add(value);
    }

    /**
     * Liefert die letzten n Werte.<br>
     */
    public synchronized List<T> getLastValues(final int count) {
        final List<T> lastValues = newValues;
        newValues = null;

        if (lastValues != null) {
            // Neue Werte hinzufügen.
            valueList.addAll(lastValues);
        }

        // Alte Werte entfernen.
        final int n = Math.min(count, valueList.size());

        while (valueList.size() > n) {
            final T oldValue = valueList.removeFirst();

            treeSet.remove(oldValue);
        }

        if (lastValues != null) {
            // Neue Werte für min.-/max. hinzufügen.
            treeSet.addAll(lastValues);
        }

        return valueList;
    }

    public T getMaxValue() {
        return treeSet.last();
    }

    public T getMinValue() {
        return treeSet.first();
    }
}
