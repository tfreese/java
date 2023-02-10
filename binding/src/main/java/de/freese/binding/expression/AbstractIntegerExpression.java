// Created: 01.08.2018
package de.freese.binding.expression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractIntegerExpression extends AbstractNumberExpression<Integer> implements IntegerExpression {
    /**
     * @see de.freese.binding.value.ObservableIntegerValue#get()
     */
    @Override
    public int get() {
        return intValue();
    }
}
