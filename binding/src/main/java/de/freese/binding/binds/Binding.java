// Created: 31.07.2018
package de.freese.binding.binds;

import de.freese.binding.property.Property;
import de.freese.binding.value.ChangeListener;
import de.freese.binding.value.ObservableValue;

/**
 * Analog: javafx.beans.binding.Binding
 *
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public interface Binding<T> extends ObservableValue<T>
{
    // @SuppressWarnings("unchecked")
    // public void bind(ObservableValue<? extends Object>...dependencies);

    // public void unbind();

    /**
     * Aktualisiert den Wert durch die Werte der registrierten {@link Property}<br>
     * und feuert die {@link ChangeListener}, falls die Werte unterschiedlich sind.
     */
    void update();
}
