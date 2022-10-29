// Created: 01.08.2018
package de.freese.binding.expression;

import de.freese.binding.Bindings;
import de.freese.binding.binds.BooleanBinding;
import de.freese.binding.binds.NumberBinding;
import de.freese.binding.constant.DoubleConstant;
import de.freese.binding.constant.FloatConstant;
import de.freese.binding.constant.IntegerConstant;
import de.freese.binding.constant.LongConstant;
import de.freese.binding.value.ObservableNumberValue;

/**
 * @author Thomas Freese
 *
 * @param <T> Konkreter Typ
 */
public interface NumberExpression<T extends Number> extends ObservableNumberValue<T>
{
    /**
     * @param value double
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> add(final double value)
    {
        return Bindings.add(this, DoubleConstant.valueOf(value));
    }

    /**
     * @param value float
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> add(final float value)
    {
        return Bindings.add(this, FloatConstant.valueOf(value));
    }

    /**
     * @param value int
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> add(final int value)
    {
        return Bindings.add(this, IntegerConstant.valueOf(value));
    }

    /**
     * @param value long
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> add(final long value)
    {
        return Bindings.add(this, LongConstant.valueOf(value));
    }

    /**
     * @param other {@link ObservableNumberValue}
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> add(final ObservableNumberValue<? extends Number> other)
    {
        return Bindings.add(this, other);
    }

    /**
     * @param value double
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> divide(final double value)
    {
        return Bindings.divide(this, DoubleConstant.valueOf(value));
    }

    /**
     * @param value float
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> divide(final float value)
    {
        return Bindings.divide(this, FloatConstant.valueOf(value));
    }

    /**
     * @param value int
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> divide(final int value)
    {
        return Bindings.divide(this, IntegerConstant.valueOf(value));
    }

    /**
     * @param value long
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> divide(final long value)
    {
        return Bindings.divide(this, LongConstant.valueOf(value));
    }

    /**
     * @param other {@link ObservableNumberValue}
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> divide(final ObservableNumberValue<? extends Number> other)
    {
        return Bindings.divide(this, other);
    }

    /**
     * @return {@link BooleanBinding}
     */
    default BooleanBinding isNotNull()
    {
        return Bindings.isNotNull(this);
    }

    /**
     * @return {@link BooleanBinding}
     */
    default BooleanBinding isNull()
    {
        return Bindings.isNull(this);
    }

    /**
     * @param value double
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> multiply(final double value)
    {
        return Bindings.multiply(this, DoubleConstant.valueOf(value));
    }

    /**
     * @param value float
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> multiply(final float value)
    {
        return Bindings.multiply(this, FloatConstant.valueOf(value));
    }

    /**
     * @param value int
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> multiply(final int value)
    {
        return Bindings.multiply(this, IntegerConstant.valueOf(value));
    }

    /**
     * @param value long
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> multiply(final long value)
    {
        return Bindings.multiply(this, LongConstant.valueOf(value));
    }

    /**
     * @param other {@link ObservableNumberValue}
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> multiply(final ObservableNumberValue<? extends Number> other)
    {
        return Bindings.multiply(this, other);
    }

    /**
     * @param value double
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> subtract(final double value)
    {
        return Bindings.subtract(this, DoubleConstant.valueOf(value));
    }

    /**
     * @param value float
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> subtract(final float value)
    {
        return Bindings.subtract(this, FloatConstant.valueOf(value));
    }

    /**
     * @param value int
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> subtract(final int value)
    {
        return Bindings.subtract(this, IntegerConstant.valueOf(value));
    }

    /**
     * @param value long
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> subtract(final long value)
    {
        return Bindings.subtract(this, LongConstant.valueOf(value));
    }

    /**
     * @param other {@link ObservableNumberValue}
     *
     * @return {@link NumberBinding}
     */
    default NumberBinding<? extends Number> subtract(final ObservableNumberValue<? extends Number> other)
    {
        return Bindings.subtract(this, other);
    }
}
