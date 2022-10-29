// Created: 31.07.2018
package de.freese.binding.expression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractBooleanExpression extends AbstractExpression<Boolean> implements BooleanExpression
{
    /**
     * @see de.freese.binding.value.ObservableBooleanValue#get()
     */
    @Override
    public boolean get()
    {
        return getValue() != null && getValue();
    }
}
