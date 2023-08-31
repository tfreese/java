// Created: 01.08.2018
package de.freese.binding.expression;

/**
 * @author Thomas Freese
 */
public abstract class AbstractDoubleExpression extends AbstractNumberExpression<Double> implements DoubleExpression {

    @Override
    public double get() {
        return doubleValue();
    }
}
