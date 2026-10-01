package de.freese.jconky.sensor.cpu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.freese.jconky.sensor.AbstractSensor;

/**
 * @author Thomas Freese
 * @since 27.09.26
 */
public class CpuSensor extends AbstractSensor {
    // /**
    //  * sensors: Tdie
    //  */
    // private static final Pattern SENSORS_INTEL_PATTERN = Pattern.compile("Tdie:\\s+.*", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    /**
     * An AMD CPU do not have temperatures for each core.<br>
     * sensors: k10temp-pci-*
     */
    static final Pattern SENSORS_AMD_PATTERN = Pattern.compile("Tctl:.*", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    //
    // private static final com.sun.management.OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN =
    //         (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private static CpuTimes parseCpuTimes(final String line) {
        final String[] splits = SPACE_PATTERN.split(line);

        final long user = Long.parseLong(splits[1]);
        final long nice = Long.parseLong(splits[2]);
        final long system = Long.parseLong(splits[3]);
        final long idle = Long.parseLong(splits[4]);
        final long ioWait = Long.parseLong(splits[5]);
        final long irq = Long.parseLong(splits[6]);
        final long softIrq = Long.parseLong(splits[7]);
        final long steal = Long.parseLong(splits[8]);
        final long guest = Long.parseLong(splits[9]);
        final long guestNice = Long.parseLong(splits[10]);

        return new CpuTimes(user, nice, system, idle, ioWait, irq, softIrq, steal, guest, guestNice);
    }

    private final Map<Integer, CpuTimes> previous = new HashMap<>();
    private Cpu cpu;

    public Cpu getCpu() {
        return cpu;
    }

    @Override
    public void update() {
        final Map<Integer, Double> temperatures = getTemperatures();

        // Times
        final List<String> lines = readContent("/proc/stat");

        // Total Jiffies.
        String line = lines.getFirst();

        CpuTimes cpuTimes = parseCpuTimes(line);
        double usage = cpuTimes.calculateCpuUsage(previous.getOrDefault(-1, null));
        previous.put(-1, cpuTimes);

        final Cpu cpuNew = new Cpu(temperatures.getOrDefault(-1, 0D), usage, getLoadAvg());

        final int numCpus = Runtime.getRuntime().availableProcessors();
        // final int numCpus = OPERATING_SYSTEM_MX_BEAN.getAvailableProcessors();
        final Map<Integer, Integer> frequencies = getFrequencies(numCpus);

        // Core Jiffies.
        for (int i = 0; i < numCpus; i++) {
            line = lines.get(i + 1);

            cpuTimes = parseCpuTimes(line);
            usage = cpuTimes.calculateCpuUsage(previous.getOrDefault(i, null));
            previous.put(i, cpuTimes);

            final double temperature = temperatures.getOrDefault(i, 0D);
            final int frequency = frequencies.getOrDefault(i, 0);

            final CpuCore cpuCore = new CpuCore(i, frequency, temperature, usage);
            cpuNew.addCore(cpuCore);
        }

        cpu = cpuNew;
    }

    private Map<Integer, Integer> getFrequencies(final int numCpus) {
        final Map<Integer, Integer> frequencies = new HashMap<>();

        for (int i = 0; i < numCpus; i++) {
            final String file = String.format("/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq", i);
            final List<String> lines = readContent(file);

            // Nur eine Zeile erwartet.
            final String line = lines.getFirst();

            final int frequency = Integer.parseInt(line);

            frequencies.put(i, frequency);
        }

        return frequencies;
    }

    private CpuLoadAvg getLoadAvg() {
        final List<String> lines = readContent("/proc/loadavg");
        final String line = lines.getFirst();

        // String[] splits = line.split(SPACE_PATTERN.pattern());
        final String[] splits = SPACE_PATTERN.split(line);

        return new CpuLoadAvg(Double.parseDouble(splits[0]), Double.parseDouble(splits[1]), Double.parseDouble(splits[2]));

        // return new CpuLoadAvg(
        //         OPERATING_SYSTEM_MX_BEAN.getSystemLoadAverage() * 10D,
        //         OPERATING_SYSTEM_MX_BEAN.getCpuLoad() * 10D,
        //         OPERATING_SYSTEM_MX_BEAN.getProcessCpuLoad() * 10D);
    }

    private Map<Integer, Double> getTemperatures() {
        final ProcessBuilder processBuilderSensors = new ProcessBuilder().command("/bin/sh", "-c", "sensors");
        final String output = String.join(System.lineSeparator(), readContent(processBuilderSensors));

        final Map<Integer, Double> temperatures = new HashMap<>();

        // Intel
        // final Matcher matcher = SENSORS_INTEL_PATTERN.matcher(output);
        //
        // if (matcher.find()) {
        //     final String line = matcher.group();
        //
        //     final String[] splits = SPACE_PATTERN.split(line);
        //
        //     String temperatureString = splits[1];
        //     temperatureString = temperatureString.replace("+", "").replace("°C", "");
        //     final double temperature = Double.parseDouble(temperatureString);
        //
        //     temperatures.put(-1, temperature);
        // }

        // An AMD CPU do not have temperatures for each core.
        final Matcher matcher = SENSORS_AMD_PATTERN.matcher(output);

        while (matcher.find()) {
            final String line = matcher.group();

            final String[] splits = SPACE_PATTERN.split(line);

            final String temperatureString = splits[1].replace("+", "").replace("°C", "");

            final double temperature = Double.parseDouble(temperatureString);

            temperatures.put(-1, temperature);
        }

        return temperatures;
    }
}
