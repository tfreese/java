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
import io.micrometer.core.instrument.Metrics;
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
public final class MeterExporter
{
    /**
     * @param step {@link Duration}; see {@link PushRegistryConfig#step()}
     * @param baseTimeUnit {@link TimeUnit}; see {@link MeterRegistry#getBaseTimeUnit()}
     *
     * @see Metrics#globalRegistry#getMeters()
     */
    public static List<String> export(MeterRegistry registry, final Duration step, TimeUnit baseTimeUnit)
    {
        List<String> list = new ArrayList<>();

        Comparator<Meter> comparator = Comparator.comparing(m -> m.getId().getName());

        registry.getMeters().stream()
                .sorted(comparator).forEach(m -> m.use(gauge ->
                {
                    String text = id(gauge) + ": value=" + value(gauge, gauge.value());

                    list.add(text);
                }, counter ->
                {
                    double count = counter.count();

                    if (Double.compare(count, 0.0D) == 0)
                    {
                        String text = id(counter) + ": throughput=0";
                        list.add(text);
                        return;
                    }

                    String text = id(counter) + ": throughput=" + rate(counter, step, count);
                    list.add(text);
                }, timer ->
                {
                    HistogramSnapshot snapshot = timer.takeSnapshot();
                    long count = snapshot.count();

                    if (count == 0)
                    {
                        String text = id(timer) + ": throughput=0";
                        list.add(text);
                        return;
                    }

                    String text = id(timer) + ": throughput=" + unitlessRate(step, count) + "; mean="
                            + time(baseTimeUnit, snapshot.mean(baseTimeUnit)) + "; max="
                            + time(baseTimeUnit, snapshot.max(baseTimeUnit));
                    list.add(text);
                }, summary ->
                {
                    HistogramSnapshot snapshot = summary.takeSnapshot();
                    long count = snapshot.count();

                    if (count == 0)
                    {
                        String text = id(summary) + ": throughput=0";
                        list.add(text);
                        return;
                    }

                    String text = id(summary) + ": throughput=" + unitlessRate(step, count) + "; mean="
                            + value(summary, snapshot.mean()) + "; max=" + value(summary, snapshot.max());
                    list.add(text);
                }, longTaskTimer ->
                {
                    int activeTasks = longTaskTimer.activeTasks();

                    if (activeTasks == 0)
                    {
                        String text = id(longTaskTimer) + ": active=0";
                        list.add(text);
                        return;
                    }

                    String text = id(longTaskTimer) + ": active=" + value(longTaskTimer, activeTasks) + "; duration="
                            + time(baseTimeUnit, longTaskTimer.duration(baseTimeUnit)) + "; mean="
                            + time(baseTimeUnit, longTaskTimer.mean(baseTimeUnit)) + "; max="
                            + time(baseTimeUnit, longTaskTimer.max(baseTimeUnit));
                    list.add(text);
                }, timeGauge ->
                {
                    double value = timeGauge.value(baseTimeUnit);

                    if ((value == 0))
                    {
                        String text = id(timeGauge) + ": value=0";
                        list.add(text);
                        return;
                    }

                    String text = id(timeGauge) + ": value=" + time(baseTimeUnit, value);
                    list.add(text);
                }, functionCounter ->
                {
                    double count = functionCounter.count();

                    if (count == 0)
                    {
                        String text = id(functionCounter) + ": throughput=0";
                        list.add(text);
                        return;
                    }

                    String text = id(functionCounter) + ": throughput=" + rate(functionCounter, step, count);
                    list.add(text);
                }, functionTimer ->
                {
                    double count = functionTimer.count();

                    if (count == 0)
                    {
                        String text = id(functionTimer) + ": throughput=0";
                        list.add(text);
                        return;
                    }

                    String text = id(functionTimer) + ": throughput=" + rate(functionTimer, step, count) + "; mean="
                            + time(baseTimeUnit, functionTimer.mean(baseTimeUnit));
                    list.add(text);
                }, meter -> list.add(writeMeter(meter, step, baseTimeUnit))));

        return list;
    }

    private static String humanReadableBaseUnit(final Meter meter, final double value)
    {
        String baseUnit = meter.getId().getBaseUnit();

        if (baseUnit == null)
        {
            return DoubleFormat.decimalOrNan(value);
        }

        if (BaseUnits.BYTES.equals(baseUnit))
        {
            return humanReadableByteCount(value);
        }

        return DoubleFormat.decimalOrNan(value) + " " + baseUnit;
    }

    /**
     * See <a href="https://stackoverflow.com/a/3758880/510017">stackoverflow</a>.
     */
    private static String humanReadableByteCount(final double bytes)
    {
        int unit = 1024;

        if ((bytes < unit) || Double.isNaN(bytes))
        {
            return DoubleFormat.decimalOrNan(bytes) + " B";
        }

        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "i";

        return DoubleFormat.decimalOrNan(bytes / Math.pow(unit, exp)) + " " + pre + "B";
    }

    private static String id(final Meter meter)
    {
        Meter.Id id = meter.getId();
        NamingConvention namingConvention = NamingConvention.dot;

        String meterIdName = NamingConvention.dot.name(id.getName(), id.getType(), id.getBaseUnit());

        if (id.getTags().isEmpty())
        {
            return meterIdName;
        }

        String meterIdTags = id.getTags().stream()
                .map(t -> Tag.of(namingConvention.tagKey(t.getKey()), namingConvention.tagValue(t.getValue())))
                .map(tag -> tag.getKey() + "=" + tag.getValue())
                .collect(Collectors.joining(",", "{", "}"));

        return meterIdName + meterIdTags;
    }

    private static String rate(final Meter meter, final Duration step, final double rate)
    {
        return humanReadableBaseUnit(meter, rate / step.getSeconds()) + "/s";
    }

    private static String time(final TimeUnit timeUnit, final double time)
    {
        return TimeUtils.format(Duration.ofNanos((long) TimeUtils.convert(time, timeUnit, TimeUnit.NANOSECONDS)));
    }

    private static String unitlessRate(final Duration step, final double rate)
    {
        return DoubleFormat.decimalOrNan(rate / step.getSeconds()) + "/s";
    }

    private static String value(final Meter meter, final double value)
    {
        return humanReadableBaseUnit(meter, value);
    }

    private static String writeMeter(final Meter meter, final Duration step, TimeUnit baseTimeUnit)
    {
        return StreamSupport.stream(meter.measure().spliterator(), false).map(ms ->
        {
            String msLine = ms.getStatistic().getTagValueRepresentation() + "=";

            return switch (ms.getStatistic())
                    {
                        case TOTAL, MAX, VALUE -> msLine + value(meter, ms.getValue());
                        case TOTAL_TIME, DURATION -> msLine + time(baseTimeUnit, ms.getValue());
                        case COUNT -> "throughput=" + rate(meter, step, ms.getValue());
                        default -> msLine + DoubleFormat.decimalOrNan(ms.getValue());
                    };
        }).collect(Collectors.joining("; ", id(meter) + ": ", ""));
    }

    private MeterExporter()
    {
        super();
    }
}
