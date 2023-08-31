// Created: 31.07.2018
package de.freese.binding.property;

import de.freese.binding.collections.ObservableList;

/**
 * @author Thomas Freese
 */
public class SimpleListProperty<T> extends AbstractListProperty<T> {

    private final Object bean;

    private final String name;

    public SimpleListProperty() {
        this(null, null, null);
    }

    public SimpleListProperty(final Object bean, final String name) {
        this(bean, name, null);
    }

    public SimpleListProperty(final Object bean, final String name, final ObservableList<T> initialValue) {
        super();

        this.bean = bean; // Objects.requireNonNull(bean, "bean required");
        this.name = name;

        setValue(initialValue);
    }

    @Override
    public Object getBean() {
        return this.bean;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
