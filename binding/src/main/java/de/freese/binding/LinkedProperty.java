package de.freese.binding;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Thomas Freese
 * @since 19.09.26
 */
public class LinkedProperty<T> implements ChangeListener<T> {
    private final Function<Set<Property<T>>, T> valueGenerator;
    
    private Set<Property<T>> properties = new LinkedHashSet<>();

    private T value = null;

    public LinkedProperty(final Function<Set<Property<T>>, T> valueGenerator) {
        super();

        this.valueGenerator = Objects.requireNonNull(valueGenerator, "valueGenerator required");
    }

    @Override
    public void changed(final Property<T> property, final T oldValue, final T newValue) {
        generateValue();
    }

    public T getValue() {
        return value;
    }

    public void linkProperty(final Property<T> property) {
        final Set<Property<T>> props = new LinkedHashSet<>(properties);
        props.add(property);
        properties = Set.copyOf(props);

        generateValue();

        property.addListener(this);
    }

    public void unlinkProperty(final Property<T> property) {
        property.removeListener(this);

        final Set<Property<T>> props = new LinkedHashSet<>(properties);
        props.remove(property);
        properties = Set.copyOf(props);

        generateValue();
    }

    private void generateValue() {
        value = valueGenerator.apply(properties);
    }
}
