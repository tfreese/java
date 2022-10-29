// Created: 23.07.2018
package de.freese.metamodel.metagen.model;

import java.util.Objects;

/**
 * Enthält die MetaDaten einer Tabelle.
 *
 * @author Thomas Freese
 */
public class Sequence
{
    /**
     *
     */
    private long increment;
    /**
    *
    */
    private String name;
    /**
     *
     */
    private long nextValue;
    /**
    *
    */
    private Schema schema;
    /**
     *
     */
    private long startWith;

    /**
     * Erstellt ein neues {@link Sequence} Object.
     *
     * @param schema {@link Schema}
     * @param name String
     */
    Sequence(final Schema schema, final String name)
    {
        super();

        this.schema = Objects.requireNonNull(schema, "schema required");
        this.name = Objects.requireNonNull(name, "name required");
    }

    /**
     * @return long
     */
    public long getIncrement()
    {
        return this.increment;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return long
     */
    public long getNextValue()
    {
        return this.nextValue;
    }

    /**
     * @return {@link Schema}
     */
    public Schema getSchema()
    {
        return this.schema;
    }

    /**
     * @return long
     */
    public long getStartWith()
    {
        return this.startWith;
    }

    /**
     * @param increment long
     */
    public void setIncrement(final long increment)
    {
        this.increment = increment;
    }

    /**
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @param nextValue long
     */
    public void setNextValue(final long nextValue)
    {
        this.nextValue = nextValue;
    }

    /**
     * @param schema {@link Schema}
     */
    public void setSchema(final Schema schema)
    {
        this.schema = schema;
    }

    /**
     * @param startWith long
     */
    public void setStartWith(final long startWith)
    {
        this.startWith = startWith;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Sequence [");
        builder.append("schema=").append(getSchema().getName());
        builder.append(", name=").append(getName());
        builder.append("]");

        return builder.toString();
    }
}
