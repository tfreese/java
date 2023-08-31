// Created: 01.08.2018
package de.freese.binding.expression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractIntegerExpression extends AbstractNumberExpression<Integer> implements IntegerExpression {

    @Override
    public int get() {
        return intValue();
    }
}
