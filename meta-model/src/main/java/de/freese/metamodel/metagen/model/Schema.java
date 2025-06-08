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
public class Schema {
    private final Map<String, Sequence> sequences = new TreeMap<>();
    private final Map<String, Table> tables = new TreeMap<>();

    private String name;

    public String getName() {
        return name;
    }

    public Sequence getSequence(final String name) {
        return sequences.computeIfAbsent(name, key -> new Sequence(this, key));
    }

    public List<Sequence> getSequences() {
        // return sequences.values().stream().sorted(Comparator.comparing(Sequence::getName)).collect(Collectors.toList());
        return new ArrayList<>(sequences.values());
    }

    public Table getTable(final String name) {
        return tables.computeIfAbsent(name, key -> new Table(this, key));
    }

    public List<Table> getTables() {
        // return tables.values().stream().sorted(Comparator.comparing(Table::getName)).collect(Collectors.toList());
        return new ArrayList<>(tables.values());
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Schema [");
        builder.append("name=").append(getName());
        builder.append("]");

        return builder.toString();
    }

    public void validate() {
        tables.values().forEach(Table::validate);
    }
}
