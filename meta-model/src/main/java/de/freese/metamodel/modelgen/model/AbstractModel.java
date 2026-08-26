package de.freese.metamodel.modelgen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Basis-Implementierung eines Model-Objekts.
 *
 * @author Thomas Freese
 * @since 29.07.2018
 */
public abstract class AbstractModel {
    private final List<String> annotations = new ArrayList<>();
    private final List<String> comments = new ArrayList<>();
    private final String name;

    private Object payload;

    protected AbstractModel(final String name) {
        super();

        this.name = Objects.requireNonNull(name, "name required");
    }

    public void addAnnotation(final String annotation) {
        annotations.add(annotation);
    }

    public void addComment(final String comment) {
        comments.add(comment);
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public List<String> getComments() {
        return comments;
    }

    public String getName() {
        return name;
    }

    public <T> T getPayload(final Class<T> type) {
        return type.cast(payload);
    }

    public void setPayload(final Object payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " ["
                + "name = " + name
                + "]";
    }
}
