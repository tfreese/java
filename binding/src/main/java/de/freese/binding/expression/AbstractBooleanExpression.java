// Created: 31.07.2018
package de.freese.binding.expression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractBooleanExpression extends AbstractExpression<Boolean> implements BooleanExpression {

    @Override
    public boolean get() {
        return getValue() != null && getValue();
    }
}
