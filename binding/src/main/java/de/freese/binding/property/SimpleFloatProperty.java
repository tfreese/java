// Created: 31.07.2018
package de.freese.binding.property;

/**
 * @author Thomas Freese
 */
public class SimpleFloatProperty extends AbstractFloatProperty
{
    private final Object bean;

    private final String name;

    public SimpleFloatProperty()
    {
        this(null, null, 0.0F);
    }

    public SimpleFloatProperty(final Object bean, final String name)
    {
        this(bean, name, 0.0F);
    }

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
