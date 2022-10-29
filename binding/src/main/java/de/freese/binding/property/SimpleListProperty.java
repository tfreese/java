// Created: 31.07.2018
package de.freese.binding.property;

import de.freese.binding.collections.ObservableList;

/**
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public class SimpleListProperty<T> extends AbstractListProperty<T>
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
     * Erstellt ein neues {@link SimpleListProperty} Object.
     */
    public SimpleListProperty()
    {
        this(null, null, null);
    }

    /**
     * Erstellt ein neues {@link SimpleListProperty} Object.
     *
     * @param bean Object
     * @param name String
     */
    public SimpleListProperty(final Object bean, final String name)
    {
        this(bean, name, null);
    }

    /**
     * Erstellt ein neues {@link SimpleListProperty} Object.
     *
     * @param bean Object
     * @param name String
     * @param initialValue {@link ObservableList}
     */
    public SimpleListProperty(final Object bean, final String name, final ObservableList<T> initialValue)
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
