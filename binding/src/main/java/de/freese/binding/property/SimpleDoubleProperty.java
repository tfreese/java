// Created: 31.07.2018
package de.freese.binding.property;

/**
 * @author Thomas Freese
 */
public class SimpleDoubleProperty extends AbstractDoubleProperty
{
    /**
    *
    */
    private final Object bean;
    /**
    *
    */
    private final String name;

    /**
     * Erstellt ein neues {@link SimpleDoubleProperty} Object.
     */
    public SimpleDoubleProperty()
    {
        this(null, null, 0.0D);
    }

    /**
     * Erstellt ein neues {@link SimpleDoubleProperty} Object.
     *
     * @param bean Object
     * @param name String
     */
    public SimpleDoubleProperty(final Object bean, final String name)
    {
        this(bean, name, 0.0D);
    }

    /**
     * Erstellt ein neues {@link SimpleDoubleProperty} Object.
     *
     * @param bean Object
     * @param name String
     * @param initialValue double
     */
    public SimpleDoubleProperty(final Object bean, final String name, final double initialValue)
    {
        super();

        this.bean = bean; // Objects.requireNonNull(bean, "bean required");
        this.name = name;

        setValue(initialValue);
    }

    /**
     * @see de.freese.binding.property.ReadOnlyProperty#getBean()
     */
    @Override
    public Object getBean()
    {
        return this.bean;
    }

    /**
     * @see de.freese.binding.property.ReadOnlyProperty#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }
}
