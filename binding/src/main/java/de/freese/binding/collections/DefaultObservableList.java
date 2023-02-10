// Created: 09.08.2018
package de.freese.binding.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class DefaultObservableList<T> extends AbstractObservableList<T> {
    private final List<T> source;

    public DefaultObservableList() {
        this(new ArrayList<>());
    }

    public DefaultObservableList(final List<T> source) {
        super();

        this.source = Objects.requireNonNull(source, "source required");
    }

    /**
     * @see de.freese.binding.collections.AbstractObservableList#get(int)
     */
    @Override
    public T get(final int index) {
        return getSource().get(index);
    }

    public final List<T> getSource() {
        return this.source;
    }

    /**
     * @see de.freese.binding.collections.AbstractObservableList#size()
     */
    @Override
    public int size() {
        return getSource().size();
    }

    /**
     * @see de.freese.binding.collections.AbstractObservableList#doAdd(int, java.lang.Object)
     */
    @Override
    protected void doAdd(final int index, final T element) {
        getLogger().debug("Index: {}; Element: {}", index, element);

        getSource().add(index, element);
    }

    /**
     * @see de.freese.binding.collections.AbstractObservableList#doRemove(int)
     */
    @Override
    protected T doRemove(final int index) {
        getLogger().debug("Index: {}", index);

        return getSource().remove(index);
    }

    /**
     * @see de.freese.binding.collections.AbstractObservableList#doSet(int, java.lang.Object)
     */
    @Override
    protected T doSet(final int index, final T element) {
        getLogger().debug("Index: {}; Element: {}", index, element);

        return getSource().set(index, element);
    }
}
