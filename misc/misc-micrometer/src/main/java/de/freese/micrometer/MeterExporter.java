package de.freese.micrometer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.push.PushRegistryConfig;
import io.micrometer.core.instrument.util.DoubleFormat;
import io.micrometer.core.instrument.util.TimeUtils;

/**
 * Code-Base from {@link LoggingMeterRegistry}.
 *
 * @author Thomas Freese
 */
public final class MeterExporter {
    /**
     * See Metrics#globalRegistry#getMeters
     *
     * @param step {@link Duration}; see {@link PushRegistryConfig#step()}
     * @param baseTimeUnit {@link TimeUnit}; see {@link MeterRegistry#getBaseTimeUnit()}
     */
    public static List<String> export(final MeterRegistry registry, final Duration step, final TimeUnit baseTimeUnit) {
        final List<String> list = new ArrayList<>();

        final Comparator<Meter> comparator = Comparator.comparing(m -> m.getId().getName());

        registry.getMeters().stream().sorted(comparator).forEach(m -> m.use(gauge -> {
            final String text = id(gauge) + ": value=" + value(gauge, gauge.value());

            list.add(text);
        }, counter -> {
            final double count = counter.count();

            if (Double.compare(count, 0.0D) == 0) {
                final String text = id(counter) + ": throughput=0";
                list.add(text);
                return;
            }

            final String text = id(counter) + ": throughput=" + rate(counter, step, count);
            list.add(text);
        }, timer -> {
            final HistogramSnapshot snapshot = timer.takeSnapshot();
            final long count = snapshot.count();

            if (count == 0) {
                final String text = id(timer) + ": throughput=0";
                list.add(text);
                return;
            }

            final String text = id(timer) + ": throughput=" + unitlessRate(step, count) + "; mean=" + time(baseTimeUnit, snapshot.mean(baseTimeUnit)) + "; max=" + time(
                    baseTimeUnit, snapshot.max(baseTimeUnit));
            list.add(text);
        }, summary -> {
            final HistogramSnapshot snapshot = summary.takeSnapshot();
            final long count = snapshot.count();

            if (count == 0) {
                final String text = id(summary) + ": throughput=0";
                list.add(text);
                return;
            }

            final String text = id(summary) + ": throughput=" + unitlessRate(step, count) + "; mean=" + value(summary, snapshot.mean()) + "; max=" + value(summary, snapshot.max());
            list.add(text);
        }, longTaskTimer -> {
            final int activeTasks = longTaskTimer.activeTasks();

            if (activeTasks == 0) {
                final String text = id(longTaskTimer) + ": active=0";
                list.add(text);
                return;
            }

            final String text = id(longTaskTimer) + ": active=" + value(longTaskTimer, activeTasks) + "; duration=" + time(baseTimeUnit,
                    longTaskTimer.duration(baseTimeUnit)) + "; mean=" + time(baseTimeUnit, longTaskTimer.mean(baseTimeUnit)) + "; max=" + time(baseTimeUnit,
                    longTaskTimer.max(baseTimeUnit));
            list.add(text);
        }, timeGauge -> {
            final double value = timeGauge.value(baseTimeUnit);

            if ((value == 0)) {
                final String text = id(timeGauge) + ": value=0";
                list.add(text);
                return;
            }

            final String text = id(timeGauge) + ": value=" + time(baseTimeUnit, value);
            list.add(text);
        }, functionCounter -> {
            final double count = functionCounter.count();

            if (count == 0) {
                final String text = id(functionCounter) + ": throughput=0";
                list.add(text);
                return;
            }

            final String text = id(functionCounter) + ": throughput=" + rate(functionCounter, step, count);
            list.add(text);
        }, functionTimer -> {
            final double count = functionTimer.count();

            if (count == 0) {
                final String text = id(functionTimer) + ": throughput=0";
                list.add(text);
                return;
            }

            final String text = id(functionTimer) + ": throughput=" + rate(functionTimer, step, count) + "; mean=" + time(baseTimeUnit, functionTimer.mean(baseTimeUnit));
            list.add(text);
        }, meter -> list.add(writeMeter(meter, step, baseTimeUnit))));

        return list;
    }

    private static String humanReadableBaseUnit(final Meter meter, final double value) {
        final String baseUnit = meter.getId().getBaseUnit();

        if (baseUnit == null) {
            return DoubleFormat.decimalOrNan(value);
        }

        if (BaseUnits.BYTES.equals(baseUnit)) {
            return humanReadableByteCount(value);
        }

        return DoubleFormat.decimalOrNan(value) + " " + baseUnit;
    }

    /**
     * See <a href="https://stackoverflow.com/a/3758880/510017">stackoverflow</a>.
     */
    private static String humanReadableByteCount(final double bytes) {
        final int unit = 1024;

        if ((bytes < unit) || Double.isNaN(bytes)) {
            return DoubleFormat.decimalOrNan(bytes) + " B";
        }

        final int exp = (int) (Math.log(bytes) / Math.log(unit));
        final String pre = "KMGTPE".charAt(exp - 1) + "i";

        return DoubleFormat.decimalOrNan(bytes / Math.pow(unit, exp)) + " " + pre + "B";
    }

    private static String id(final Meter meter) {
        final Meter.Id id = meter.getId();
        final NamingConvention namingConvention = NamingConvention.dot;

        final String meterIdName = NamingConvention.dot.name(id.getName(), id.getType(), id.getBaseUnit());

        if (id.getTags().isEmpty()) {
            return meterIdName;
        }

        final String meterIdTags = id.getTags().stream().map(t -> Tag.of(namingConvention.tagKey(t.getKey()), namingConvention.tagValue(t.getValue())))
                .map(tag -> tag.getKey() + "=" + tag.getValue()).collect(Collectors.joining(",", "{", "}"));

        return meterIdName + meterIdTags;
    }

    private static String rate(final Meter meter, final Duration step, final double rate) {
        return humanReadableBaseUnit(meter, rate / step.getSeconds()) + "/s";
    }

    private static String time(final TimeUnit timeUnit, final double time) {
        return TimeUtils.format(Duration.ofNanos((long) TimeUtils.convert(time, timeUnit, TimeUnit.NANOSECONDS)));
    }

    private static String unitlessRate(final Duration step, final double rate) {
        return DoubleFormat.decimalOrNan(rate / step.getSeconds()) + "/s";
    }

    private static String value(final Meter meter, final double value) {
        return humanReadableBaseUnit(meter, value);
    }

    private static String writeMeter(final Meter meter, final Duration step, final TimeUnit baseTimeUnit) {
        return StreamSupport.stream(meter.measure().spliterator(), false).map(ms -> {
            final String msLine = ms.getStatistic().getTagValueRepresentation() + "=";

            return switch (ms.getStatistic()) {
                case TOTAL, MAX, VALUE -> msLine + value(meter, ms.getValue());
                case TOTAL_TIME, DURATION -> msLine + time(baseTimeUnit, ms.getValue());
                case COUNT -> "throughput=" + rate(meter, step, ms.getValue());
                default -> msLine + DoubleFormat.decimalOrNan(ms.getValue());
            };
        }).collect(Collectors.joining("; ", id(meter) + ": ", ""));
    }

    private MeterExporter() {
        super();
    }
}
