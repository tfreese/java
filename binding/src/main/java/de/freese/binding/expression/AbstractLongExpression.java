// Created: 01.08.2018
package de.freese.binding.expression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractLongExpression extends AbstractNumberExpression<Long> implements LongExpression
{
    /**
     * @see de.freese.binding.value.ObservableIntegerValue#get()
     */
    @Override
    public long get()
    {
        return longValue();
    }
}
