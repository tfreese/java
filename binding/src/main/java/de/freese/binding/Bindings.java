// Created: 31.07.2018
package de.freese.binding;

import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import de.freese.binding.binds.AbstractBooleanBinding;
import de.freese.binding.binds.AbstractDoubleBinding;
import de.freese.binding.binds.AbstractFloatBinding;
import de.freese.binding.binds.AbstractIntegerBinding;
import de.freese.binding.binds.AbstractLongBinding;
import de.freese.binding.binds.AbstractStringBinding;
import de.freese.binding.binds.Binding;
import de.freese.binding.binds.BooleanBinding;
import de.freese.binding.binds.DoubleBinding;
import de.freese.binding.binds.FloatBinding;
import de.freese.binding.binds.IntegerBinding;
import de.freese.binding.binds.LongBinding;
import de.freese.binding.binds.NumberBinding;
import de.freese.binding.binds.StringBinding;
import de.freese.binding.value.ObservableBooleanValue;
import de.freese.binding.value.ObservableDoubleValue;
import de.freese.binding.value.ObservableFloatValue;
import de.freese.binding.value.ObservableLongValue;
import de.freese.binding.value.ObservableNumberValue;
import de.freese.binding.value.ObservableStringValue;
import de.freese.binding.value.ObservableValue;

/**
 * Util-Klasse für die {@link Binding} Implementierungen.
 *
 * @author Thomas Freese
 */
public final class Bindings {

    public static NumberBinding<? extends Number> add(final ObservableNumberValue<? extends Number> ov1, final ObservableNumberValue<? extends Number> ov2) {
        final NumberBinding<? extends Number> binding;

        if ((ov1 instanceof ObservableDoubleValue) || (ov2 instanceof ObservableDoubleValue)) {
            binding = createDoubleBinding(Double::sum, ov1, ov2);
        }
        else if ((ov1 instanceof ObservableFloatValue) || (ov2 instanceof ObservableFloatValue)) {
            binding = createFloatBinding(Float::sum, ov1, ov2);
        }
        else if ((ov1 instanceof ObservableLongValue) || (ov2 instanceof ObservableLongValue)) {
            binding = createLongBinding(Long::sum, ov1, ov2);
        }
        else {
            binding = createIntegerBinding(Integer::sum, ov1, ov2);
        }

        return binding;
    }

    public static BooleanBinding and(final ObservableBooleanValue ov1, final ObservableBooleanValue ov2) {
        final BooleanBinding binding = new AbstractBooleanBinding() {
            @Override
            protected Boolean computeValue() {
                return ov1.get() && ov2.get();
            }
        };

        ov1.addListener((observable, oldValue, newValue) -> binding.update());
        ov2.addListener((observable, oldValue, newValue) -> binding.update());

        binding.update();

        return binding;
    }

    public static StringBinding concat(final ObservableStringValue ov1, final ObservableStringValue ov2) {
        return createStringBinding((v1, v2) -> {
            if ((v1 == null) && (v2 == null)) {
                return null;
            }

            return v1 + v2;
        }, ov1, ov2);
    }

    public static BooleanBinding createBooleanBinding(final Predicate<Object> function, final ObservableValue<?> ov) {
        final BooleanBinding binding = new AbstractBooleanBinding() {
            @Override
            protected Boolean computeValue() {
                return function.test(ov.getValue());
            }
        };

        ov.addListener((observable, oldValue, newValue) -> binding.update());

        binding.update();

        return binding;
    }

    public static DoubleBinding createDoubleBinding(final BinaryOperator<Double> function, final ObservableNumberValue<? extends Number> ov1, final ObservableNumberValue<? extends Number> ov2) {
        final DoubleBinding binding = new AbstractDoubleBinding() {
            @Override
            protected double computeValue() {
                return function.apply(ov1.doubleValue(), ov2.doubleValue());
            }
        };

        ov1.addListener((observable, oldValue, newValue) -> binding.update());
        ov2.addListener((observable, oldValue, newValue) -> binding.update());

        binding.update();

        return binding;
    }

    public static FloatBinding createFloatBinding(final BinaryOperator<Float> function, final ObservableNumberValue<? extends Number> ov1, final ObservableNumberValue<? extends Number> ov2) {
        final FloatBinding binding = new AbstractFloatBinding() {
            @Override
            protected float computeValue() {
                return function.apply(ov1.floatValue(), ov2.floatValue());
            }
        };

        ov1.addListener((observable, oldValue, newValue) -> binding.update());
        ov2.addListener((observable, oldValue, newValue) -> binding.update());

        binding.update();

        return binding;
    }

    public static IntegerBinding createIntegerBinding(final BinaryOperator<Integer> function, final ObservableNumberValue<? extends Number> ov1, final ObservableNumberValue<? extends Number> ov2) {
        final IntegerBinding binding = new AbstractIntegerBinding() {
            @Override
            protected int computeValue() {
                return function.apply(ov1.intValue(), ov2.intValue());
            }
        };

        ov1.addListener((observable, oldValue, newValue) -> binding.update());
        ov2.addListener((observable, oldValue, newValue) -> binding.update());

        binding.update();

        return binding;
    }

    public static LongBinding createLongBinding(final BinaryOperator<Long> function, final ObservableNumberValue<? extends Number> ov1, final ObservableNumberValue<? extends Number> ov2) {
        final LongBinding binding = new AbstractLongBinding() {
            @Override
            protected long computeValue() {
                return function.apply(ov1.longValue(), ov2.longValue());
            }
        };

        ov1.addListener((observable, oldValue, newValue) -> binding.update());
        ov2.addListener((observable, oldValue, newValue) -> binding.update());

        binding.update();

        return binding;
    }

    public static StringBinding createStringBinding(final BinaryOperator<String> function, final ObservableStringValue ov1, final ObservableStringValue ov2) {
        final StringBinding binding = new AbstractStringBinding() {
            @Override
            protected String computeValue() {
                return function.apply(ov1.getValue(), ov2.getValue());
            }
        };

        ov1.addListener((observable, oldValue, newValue) -> binding.update());
        ov2.addListener((observable, oldValue, newValue) -> binding.update());

        binding.update();

        return binding;
    }

    public static NumberBinding<? extends Number> divide(final ObservableNumberValue<? extends Number> ov1, final ObservableNumberValue<? extends Number> ov2) {
        final NumberBinding<? extends Number> binding;

        if ((ov1 instanceof ObservableDoubleValue) || (ov2 instanceof ObservableDoubleValue)) {
            binding = createDoubleBinding((v1, v2) -> v1 / v2, ov1, ov2);
        }
        else if ((ov1 instanceof ObservableFloatValue) || (ov2 instanceof ObservableFloatValue)) {
            binding = createFloatBinding((v1, v2) -> v1 / v2, ov1, ov2);
        }
        else if ((ov1 instanceof ObservableLongValue) || (ov2 instanceof ObservableLongValue)) {
            binding = createLongBinding((v1, v2) -> v1 / v2, ov1, ov2);
        }
        else {
            binding = createIntegerBinding((v1, v2) -> v1 / v2, ov1, ov2);
        }

        return binding;
    }

    public static BooleanBinding isBlank(final ObservableStringValue ov) {
        return createBooleanBinding(v -> getValueSafe((String) v).isBlank(), ov);
    }

    public static BooleanBinding isEmpty(final ObservableStringValue ov) {
        return createBooleanBinding(v -> getValueSafe((String) v).isEmpty(), ov);
    }

    public static BooleanBinding isNotBlank(final ObservableStringValue ov) {
        return createBooleanBinding(v -> !getValueSafe((String) v).isBlank(), ov);
    }

    public static BooleanBinding isNotEmpty(final ObservableStringValue ov) {
        return createBooleanBinding(v -> !getValueSafe((String) v).isEmpty(), ov);
    }

    public static <T> BooleanBinding isNotNull(final ObservableValue<T> ov) {
        return createBooleanBinding(Objects::nonNull, ov);
    }

    public static <T> BooleanBinding isNull(final ObservableValue<T> ov) {
        return createBooleanBinding(Objects::isNull, ov);
    }

    public static IntegerBinding length(final ObservableStringValue ov) {
        final IntegerBinding binding = new AbstractIntegerBinding() {
            @Override
            protected int computeValue() {
                return getValueSafe(ov.getValue()).length();
            }
        };

        ov.addListener((observable, oldValue, newValue) -> binding.update());

        binding.update();

        return binding;
    }

    public static NumberBinding<? extends Number> multiply(final ObservableNumberValue<? extends Number> ov1, final ObservableNumberValue<? extends Number> ov2) {
        final NumberBinding<? extends Number> binding;

        if ((ov1 instanceof ObservableDoubleValue) || (ov2 instanceof ObservableDoubleValue)) {
            binding = createDoubleBinding((v1, v2) -> v1 * v2, ov1, ov2);
        }
        else if ((ov1 instanceof ObservableFloatValue) || (ov2 instanceof ObservableFloatValue)) {
            binding = createFloatBinding((v1, v2) -> v1 * v2, ov1, ov2);
        }
        else if ((ov1 instanceof ObservableLongValue) || (ov2 instanceof ObservableLongValue)) {
            binding = createLongBinding((v1, v2) -> v1 * v2, ov1, ov2);
        }
        else {
            binding = createIntegerBinding((v1, v2) -> v1 * v2, ov1, ov2);
        }

        return binding;
    }

    public static BooleanBinding not(final ObservableBooleanValue ov) {
        final BooleanBinding binding = new AbstractBooleanBinding() {
            @Override
            protected Boolean computeValue() {
                return !ov.get();
            }
        };

        ov.addListener((observable, oldValue, newValue) -> binding.update());

        binding.update();

        return binding;
    }

    public static BooleanBinding or(final ObservableBooleanValue ov1, final ObservableBooleanValue ov2) {
        final BooleanBinding binding = new AbstractBooleanBinding() {
            @Override
            protected Boolean computeValue() {
                return ov1.get() || ov2.get();
            }
        };

        ov1.addListener((observable, oldValue, newValue) -> binding.update());
        ov2.addListener((observable, oldValue, newValue) -> binding.update());

        binding.update();

        return binding;
    }

    public static NumberBinding<? extends Number> subtract(final ObservableNumberValue<? extends Number> ov1, final ObservableNumberValue<? extends Number> ov2) {
        final NumberBinding<? extends Number> binding;

        if ((ov1 instanceof ObservableDoubleValue) || (ov2 instanceof ObservableDoubleValue)) {
            binding = createDoubleBinding((v1, v2) -> v1 - v2, ov1, ov2);
        }
        else if ((ov1 instanceof ObservableFloatValue) || (ov2 instanceof ObservableFloatValue)) {
            binding = createFloatBinding((v1, v2) -> v1 - v2, ov1, ov2);
        }
        else if ((ov1 instanceof ObservableLongValue) || (ov2 instanceof ObservableLongValue)) {
            binding = createLongBinding((v1, v2) -> v1 - v2, ov1, ov2);
        }
        else {
            binding = createIntegerBinding((v1, v2) -> v1 - v2, ov1, ov2);
        }

        return binding;
    }

    /**
     * Liefert einen leeren String "", wenn null.
     */
    private static String getValueSafe(final String value) {
        return value == null ? "" : value;
    }

    private Bindings() {
        super();
    }
}
