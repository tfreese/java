// Created: 31.07.2018
package de.freese.binding.property;

/**
 * @author Thomas Freese
 */
public class SimpleBooleanProperty extends AbstractBooleanProperty
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
     * Erstellt ein neues {@link SimpleBooleanProperty} Object.
     */
    public SimpleBooleanProperty()
    {
        this(null, null, false);
    }

    /**
     * Erstellt ein neues {@link SimpleBooleanProperty} Object.
     *
     * @param bean Object
     * @param name String
     */
    public SimpleBooleanProperty(final Object bean, final String name)
    {
        this(bean, name, false);
    }

    /**
     * Erstellt ein neues {@link SimpleBooleanProperty} Object.
     *
     * @param bean Object
     * @param name String
     * @param initialValue boolean
     */
    public SimpleBooleanProperty(final Object bean, final String name, final boolean initialValue)
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
