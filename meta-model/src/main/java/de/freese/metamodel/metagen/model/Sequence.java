// Created: 23.07.2018
package de.freese.metamodel.metagen.model;

import java.util.Objects;

/**
 * Enthält die MetaDaten einer Tabelle.
 *
 * @author Thomas Freese
 */
public class Sequence {
    private long increment;
    private String name;
    private long nextValue;
    private Schema schema;
    private long startWith;

    Sequence(final Schema schema, final String name) {
        super();

        this.schema = Objects.requireNonNull(schema, "schema required");
        this.name = Objects.requireNonNull(name, "name required");
    }

    public long getIncrement() {
        return increment;
    }

    public String getName() {
        return name;
    }

    public long getNextValue() {
        return nextValue;
    }

    public Schema getSchema() {
        return schema;
    }

    public long getStartWith() {
        return startWith;
    }

    public void setIncrement(final long increment) {
        this.increment = increment;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setNextValue(final long nextValue) {
        this.nextValue = nextValue;
    }

    public void setSchema(final Schema schema) {
        this.schema = schema;
    }

    public void setStartWith(final long startWith) {
        this.startWith = startWith;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Sequence [");
        builder.append("schema=").append(getSchema().getName());
        builder.append(", name=").append(getName());
        builder.append("]");

        return builder.toString();
    }
}
