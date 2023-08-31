// Created: 31.07.2018
package de.freese.binding.property;

/**
 * @author Thomas Freese
 */
public class SimpleLongProperty extends AbstractLongProperty {

    private final Object bean;

    private final String name;

    public SimpleLongProperty() {
        this(null, null, 0);
    }

    public SimpleLongProperty(final Object bean, final String name) {
        this(bean, name, 0);
    }

    public SimpleLongProperty(final Object bean, final String name, final long initialValue) {
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
