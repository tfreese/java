// Created: 31.07.2018
package de.freese.binding.property;

/**
 * @author Thomas Freese
 */
public class SimpleIntegerProperty extends AbstractIntegerProperty
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
     * Erstellt ein neues {@link SimpleIntegerProperty} Object.
     */
    public SimpleIntegerProperty()
    {
        this(null, null, 0);
    }

    /**
     * Erstellt ein neues {@link SimpleIntegerProperty} Object.
     *
     * @param bean Object
     * @param name String
     */
    public SimpleIntegerProperty(final Object bean, final String name)
    {
        this(bean, name, 0);
    }

    /**
     * Erstellt ein neues {@link SimpleIntegerProperty} Object.
     *
     * @param bean Object
     * @param name String
     * @param initialValue int
     */
    public SimpleIntegerProperty(final Object bean, final String name, final int initialValue)
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
