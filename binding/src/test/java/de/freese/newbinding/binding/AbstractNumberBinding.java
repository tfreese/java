// Created: 11 Aug. 2025
package de.freese.newbinding.binding;

import de.freese.binding.binds.NumberBinding;

/**
 * @author Thomas Freese
 */
public abstract class AbstractNumberBinding extends AbstractBinding<Number> {
    protected AbstractNumberBinding() {
        super(null);
    }

    protected AbstractNumberBinding(final String name) {
        super(name);
    }

    NumberBinding<? extends Number> add(final Number value) {
        // return Bindings.add(this, new NumberConstant(value));
        return null;
    }
}
