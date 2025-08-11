// Created: 11 Aug. 2025
package de.freese.newbinding.constant;

/**
 * @author Thomas Freese
 */
public class StringConstant extends AbstractConstant<String> {
    public StringConstant(final String value) {
        this(value, null);
    }

    public StringConstant(final String value, final String name) {
        super(value, name);
    }
}
