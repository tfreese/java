// Created: 31.07.2018
package de.freese.binding.property;

/**
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public class SimpleObjectProperty<T> extends AbstractObjectProperty<T>
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
     * Erstellt ein neues {@link SimpleObjectProperty} Object.
     */
    public SimpleObjectProperty()
    {
        this(null, null, null);
    }

    /**
     * Erstellt ein neues {@link SimpleObjectProperty} Object.
     *
     * @param bean Object
     * @param name String
     */
    public SimpleObjectProperty(final Object bean, final String name)
    {
        this(bean, name, null);
    }

    /**
     * Erstellt ein neues {@link SimpleObjectProperty} Object.
     *
     * @param bean Object
     * @param name String
     * @param initialValue Object
     */
    public SimpleObjectProperty(final Object bean, final String name, final T initialValue)
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
