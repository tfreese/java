// Created: 31.07.2018
package de.freese.binding.property;

/**
 * @author Thomas Freese
 */
public class SimpleStringProperty extends AbstractStringProperty
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
     * Erstellt ein neues {@link SimpleStringProperty} Object.
     */
    public SimpleStringProperty()
    {
        this(null, null, null);
    }

    /**
     * Erstellt ein neues {@link SimpleStringProperty} Object.
     *
     * @param bean Object
     * @param name String
     */
    public SimpleStringProperty(final Object bean, final String name)
    {
        this(bean, name, null);
    }

    /**
     * Erstellt ein neues {@link SimpleStringProperty} Object.
     *
     * @param bean Object
     * @param name String
     * @param initialValue String
     */
    public SimpleStringProperty(final Object bean, final String name, final String initialValue)
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
