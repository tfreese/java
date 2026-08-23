package de.freese.binding;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 * @since 09.08.2026
 */
public class Property<T> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Property.class);
    private final Set<ChangeListener<T>> listeners = LinkedHashSet.newLinkedHashSet(4);
    private final String name;

    private T value;

    public Property() {
        this("null", null);
    }

    public Property(final String name) {
        this(name, null);
    }

    public Property(final String name, final T initialValue) {
        super();

        this.name = Objects.requireNonNull(name, "name required");
        this.value = initialValue;
    }

    public void addListener(final ChangeListener<T> listener) {
        getListeners().add(listener);
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void removeListener(final ChangeListener<T> listener) {
        getListeners().remove(listener);
    }

    public void setValue(final T value) {
        final T oldValue = this.value;
        this.value = value;

        if (!Objects.equals(oldValue, value)) {
            fireValueChangedEvent(oldValue, value);
        }
    }

    protected void fireValueChangedEvent(final T oldValue, final T newValue) {
        LOGGER.debug("Property changed for '{}' from '{}' to '{}'", getName(), oldValue, newValue);

        for (final ChangeListener<T> changeListener : getListeners()) {
            changeListener.changed(this, oldValue, newValue);
        }
    }

    protected Set<ChangeListener<T>> getListeners() {
        return listeners;
    }
}
