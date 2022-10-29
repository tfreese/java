// Created: 08.07.2018
package de.freese.metamodel.metagen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Enthält die MetaDaten eines Schemas.
 *
 * @author Thomas Freese
 */
public class Schema
{
    /**
     *
     */
    private String name;
    /**
    *
    */
    private final Map<String, Sequence> sequences = new TreeMap<>();
    /**
    *
    */
    private final Map<String, Table> tables = new TreeMap<>();

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Liefert die Sequence mit dem Namen.
     *
     * @param name String
     *
     * @return {@link Sequence}
     */
    public Sequence getSequence(final String name)
    {
        return this.sequences.computeIfAbsent(name, key -> new Sequence(this, key));
    }

    /**
     * Liefert alle Sequences des Schemas.
     *
     * @return {@link Sequence}
     */
    public List<Sequence> getSequences()
    {
        // return this.sequences.values().stream().sorted(Comparator.comparing(Sequence::getName)).collect(Collectors.toList());
        return new ArrayList<>(this.sequences.values());
    }

    /**
     * Liefert die Tabelle mit dem Namen.
     *
     * @param name String
     *
     * @return {@link Table}
     */
    public Table getTable(final String name)
    {
        return this.tables.computeIfAbsent(name, key -> new Table(this, key));
    }

    /**
     * Liefert alle Tabellen des Schemas.
     *
     * @return {@link Table}
     */
    public List<Table> getTables()
    {
        // return this.tables.values().stream().sorted(Comparator.comparing(Table::getName)).collect(Collectors.toList());
        return new ArrayList<>(this.tables.values());
    }

    /**
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Schema [");
        builder.append("name=").append(getName());
        builder.append("]");

        return builder.toString();
    }

    /**
     * Überprüfen des Models.
     */
    public void validate()
    {
        this.tables.values().forEach(Table::validate);
    }
}
