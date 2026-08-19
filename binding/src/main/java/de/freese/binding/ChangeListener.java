package de.freese.binding;

import java.util.EventListener;

@FunctionalInterface
public interface ChangeListener<T> extends EventListener {
    void changed(Property<T> property, T oldValue, T newValue);
}
