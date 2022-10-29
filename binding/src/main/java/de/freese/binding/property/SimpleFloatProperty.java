// Created: 31.07.2018
package de.freese.binding.property;

/**
 * @author Thomas Freese
 */
public class SimpleFloatProperty extends AbstractFloatProperty
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
     * Erstellt ein neues {@link SimpleFloatProperty} Object.
     */
    public SimpleFloatProperty()
    {
        this(null, null, 0.0F);
    }

    /**
     * Erstellt ein neues {@link SimpleFloatProperty} Object.
     *
     * @param bean Object
     * @param name String
     */
    public SimpleFloatProperty(final Object bean, final String name)
    {
        this(bean, name, 0.0F);
    }

    /**
     * Erstellt ein neues {@link SimpleFloatProperty} Object.
     *
     * @param bean Object
     * @param name String
     * @param initialValue float
     */
    public SimpleFloatProperty(final Object bean, final String name, final float initialValue)
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
