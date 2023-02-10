// Created: 31.07.2018
package de.freese.binding.property;

/**
 * @author Thomas Freese
 */
public class SimpleObjectProperty<T> extends AbstractObjectProperty<T> {
    private final Object bean;

    private final String name;

    public SimpleObjectProperty() {
        this(null, null, null);
    }

    public SimpleObjectProperty(final Object bean, final String name) {
        this(bean, name, null);
    }

    public SimpleObjectProperty(final Object bean, final String name, final T initialValue) {
        super();

        this.bean = bean; // Objects.requireNonNull(bean, "bean required");
        this.name = name;

        setValue(initialValue);
    }

    /**
     * @see de.freese.binding.property.ReadOnlyProperty#getBean()
     */
    @Override
    public Object getBean() {
        return this.bean;
    }

    /**
     * @see de.freese.binding.property.ReadOnlyProperty#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }
}
