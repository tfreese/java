// Created: 31.07.2018
package de.freese.binding.binds;

import java.util.Objects;

import de.freese.binding.expression.AbstractBooleanExpression;
import de.freese.binding.property.Property;

/**
 * @author Thomas Freese
 */
public abstract class AbstractBooleanBinding extends AbstractBooleanExpression implements BooleanBinding {

    // private final ChangeListener<? super Object> listener = (observable, oldValue, newValue) -> update();

    private Boolean value;

    // @SuppressWarnings("unchecked")
    // @Override
    // public void bind(final ObservableValue<? extends Object>...dependencies)
    // {
    // for (ObservableValue<? extends Object> o : dependencies)
    // {
    // o.addListener(this.listener);
    // }
    //
    // update();
    // }

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    public void update() {
        Boolean old = this.value;
        this.value = computeValue();

        if (Objects.equals(old, this.value)) {
            fireValueChangedEvent(old, this.value);
        }
    }

    /**
     * Ermittelt den neuen Wert des Bindings, falls sich einer der {@link Property} geändert hat.
     */
    protected abstract Boolean computeValue();
}
