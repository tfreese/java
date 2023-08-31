// Created: 01.08.2018
package de.freese.binding.expression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractLongExpression extends AbstractNumberExpression<Long> implements LongExpression {

    @Override
    public long get() {
        return longValue();
    }
}
