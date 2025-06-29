// Created: 31.07.2018
package de.freese.binding.property;

/**
 * @author Thomas Freese
 */
public class SimpleBooleanProperty extends AbstractBooleanProperty {

    private final Object bean;
    private final String name;

    public SimpleBooleanProperty() {
        this(null, null, false);
    }

    public SimpleBooleanProperty(final Object bean, final String name) {
        this(bean, name, false);
    }

    public SimpleBooleanProperty(final Object bean, final String name, final boolean initialValue) {
        super();

        this.bean = bean; // Objects.requireNonNull(bean, "bean required");
        this.name = name;

        setValue(initialValue);
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }
}
