package de.freese.binding;

@FunctionalInterface
public interface ChangeListener<T> {
    void changed(Property<T> property, T oldValue, T newValue);
}
