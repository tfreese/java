// Created: 11 Aug. 2025
package de.freese.newbinding.constant;

/**
 * @author Thomas Freese
 */
public class NumberConstant extends AbstractConstant<Number> {
    public NumberConstant(final Number value) {
        this(value, null);
    }

    public NumberConstant(final Number value, final String name) {
        super(value, name);
    }
}
