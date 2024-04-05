// Created: 01.09.2015
package de.freese.ga;

import java.util.Objects;

/**
 * Basisklasse eines Genoms / Gens für genetische Algorithmen.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("unchecked")
public class Gene implements Comparable<Gene> {
    public static Gene of(final Object value) {
        final Gene gene = new Gene();
        gene.setValue(value);

        return gene;
    }

    private Object value;

    @Override
    public int compareTo(final Gene o) {
        if (o == null || o == this || getClass() != o.getClass()) {
            return 0;
        }

        int comp = 0;

        if (getValue() instanceof Comparable) {
            comp = ((Comparable<Object>) getValue()).compareTo(o.getValue());
        }
        else {
            throw new IllegalStateException("GeneValue must implement Comparable");
        }

        return comp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Gene gene)) {
            return false;
        }

        return Objects.equals(getValue(), gene.getValue());
    }

    public Boolean getBoolean() {
        return (Boolean) getValue();
    }

    public Double getDouble() {
        return (Double) getValue();
    }

    public Integer getInteger() {
        return (Integer) getValue();
    }

    public Object getValue() {
        return this.value;
    }

    public <T> T getValue(final Class<T> type) {
        return type.cast(this.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(": ");
        sb.append(Objects.toString(getValue(), "null"));

        return sb.toString();
    }
}
