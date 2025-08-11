// Created: 11 Aug. 2025
package de.freese.newbinding.constant;

/**
 * @author Thomas Freese
 */
public class BooleanConstant extends AbstractConstant<Boolean> {
    public BooleanConstant(final boolean value) {
        this(value, null);
    }

    public BooleanConstant(final boolean value, final String name) {
        super(value, name);
    }
}
