// Created: 11 Aug. 2025
package de.freese.newbinding.constant;

/**
 * @author Thomas Freese
 */
public class ObjectConstant extends AbstractConstant<Object> {
    public ObjectConstant(final Object value) {
        this(value, null);
    }

    public ObjectConstant(final Object value, final String name) {
        super(value, name);
    }
}
