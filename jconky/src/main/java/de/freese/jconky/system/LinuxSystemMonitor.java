package de.freese.jconky.system;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.freese.jconky.model.CpuInfo;
import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.CpuTimes;
import de.freese.jconky.model.GpuInfo;
import de.freese.jconky.model.MusicInfo;
import de.freese.jconky.model.NetworkInfo;
import de.freese.jconky.model.NetworkInfos;
import de.freese.jconky.model.NetworkProtocolInfo;
import de.freese.jconky.model.ProcessInfo;
import de.freese.jconky.model.ProcessInfos;
import de.freese.jconky.model.TemperatureInfo;
import de.freese.jconky.model.UsageInfo;
import de.freese.jconky.util.JConkyUtils;

/**
 * @author Thomas Freese
 * @since 01.12.2020
 */
public class LinuxSystemMonitor extends AbstractSystemMonitor {
    // /**
    //  * /proc/cpuinfo: processor\\s+:\\s+(\\d+)
    //  */
    // static final Pattern CPUINFO_NUM_CPU_PATTERN = Pattern.compile("processor\\s+:\\s+(\\d+)", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    // /**
    //  * /proc/stat: cpu\\s+(.*)
    //  */
    // static final Pattern CPU_JIFFIES_PATTERN = Pattern.compile("cpu\\s+(.*)", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    // /**
    //  * Bei AMD gib's keine Temperatur pro Core.<br>
    //  * sensors: Core\\s{1}\\d+:.*
    //  */
    // static final Pattern SENSORS_CORE_PATTERN = Pattern.compile("Core\\s\\d+:.*", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    // /**
    //  * /proc/stat: cpu\\d+
    //  */
    // protected static final Pattern STAT_NUM_CPU_PATTERN = Pattern.compile("cpu\\d+", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

    private static final Pattern APROC_DIR_PATTERN = Pattern.compile("([\\d]*)");

    private static final FilenameFilter PROCESS_DIRECTORY_FILTER = (dir, name) -> {
        final File fileToTest = new File(dir, name);

        return fileToTest.isDirectory() && APROC_DIR_PATTERN.matcher(name).matches();
    };
    /**
     * sensors: Package\\s{1}id\\s{1}\\d+:.*
     */
    private static final Pattern SENSORS_PACKAGE_PATTERN = Pattern.compile("Tdie:\\s+.*", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    /**
     * /proc/%s/status: Name:\\s+(\\w+)
     */
    private static final Pattern STATUS_NAME_PATTERN = Pattern.compile("Name:\\s+(\\w+)", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    /**
     * /proc/%s/status: Uid:\\s+(\\d+)\\s.*
     */
    private static final Pattern STATUS_UID_PATTERN = Pattern.compile("Uid:\\s+(\\d+)\\s.*", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    /**
     * /proc/%s/status: VmRSS:\\s+(\\d+) kB<br>
     * residentBytes
     */
    private static final Pattern STATUS_VM_RSS_PATTERN = Pattern.compile("VmRSS:\\s+(\\d+) kB", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    // /**
    // * /proc/%s/status: VmSize:\\s+(\\d+) kB<br>
    // * totalBytes
    // */
    // private static final Pattern STATUS_VM_SIZE_MATCHER = Pattern.compile("VmSize:\\s+(\\d+) kB", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

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

    private final ProcessBuilder processBuilderCheckUpdates;
    // private final ProcessBuilder processBuilderDf;
    private final ProcessBuilder processBuilderFree;
    private final ProcessBuilder processBuilderNetworkIf;
    private final ProcessBuilder processBuilderNstat;
    private final ProcessBuilder processBuilderNvidiaSmi;
    private final ProcessBuilder processBuilderPlayerCtlMetaData;
    private final ProcessBuilder processBuilderPlayerCtlPosition;
    private final ProcessBuilder processBuilderSensors;
    private final ProcessBuilder processBuilderSmartCtl;
    private final ProcessBuilder processBuilderTop;
    // private final ProcessBuilder processBuilderUname;

    public LinuxSystemMonitor() {
        super();

        // processBuilderUname = new ProcessBuilder().command("/bin/sh", "-c", "uname --all");

        processBuilderSensors = new ProcessBuilder().command("/bin/sh", "-c", "sensors");

        // -u tommy
        processBuilderTop = new ProcessBuilder().command("/bin/sh", "-c", "top -b -n 1");

        processBuilderNetworkIf = new ProcessBuilder().command("/bin/sh", "-c", "ip -s -4 addr");
        processBuilderNstat = new ProcessBuilder("/bin/sh", "-c", "nstat -a");
        // processBuilderDf = new ProcessBuilder("/bin/sh", "-c", "df --block-size=1K");
        processBuilderFree = new ProcessBuilder("/bin/sh", "-c", "free --bytes");
        processBuilderCheckUpdates = new ProcessBuilder("/bin/sh", "-c", "checkupdates");
        processBuilderPlayerCtlMetaData = new ProcessBuilder("/bin/sh", "-c", "playerctl -s metadata");
        processBuilderPlayerCtlPosition = new ProcessBuilder("/bin/sh", "-c", "playerctl -s position");
        processBuilderSmartCtl = new ProcessBuilder("/bin/sh", "-c", "sudo smartctl -A /dev/nvme0n1");
        processBuilderNvidiaSmi = new ProcessBuilder("/bin/sh", "-c", "nvidia-smi --format=csv,noheader,nounits --query-gpu=temperature.gpu,power.draw,fan.speed,utilization.gpu");
    }

    @Override
    public CpuInfos getCpuInfos() {
        // String output = readContent("/proc/cpuinfo").stream().collect(Collectors.joining("\n"));
        //
        // int numCpus = 0;
        //
        // final Matcher matcher = CPUINFO_NUM_CPU_PATTERN.matcher(output);
        //
        // while (matcher.find()) {
        // numCpus++;
        // }

        final List<String> lines = readContent("/proc/stat");
        // String output = lines.stream().collect(Collectors.joining("\n"));
        //
        // int numCpus = 0;
        //
        // final Matcher matcher = STAT_NUM_CPU_PATTERN.matcher(output);
        //
        // while (matcher.find()) {
        // numCpus++;
        // }

        final int numCpus = Runtime.getRuntime().availableProcessors();

        // Temperaturen
        final Map<Integer, Double> temperatures = getCpuTemperatures();

        // Frequenzen
        final Map<Integer, Integer> frequencies = getCpuFrequencies(numCpus);

        final Map<Integer, CpuInfo> cpuInfoMap = new HashMap<>();

        // Total Jiffies
        String line = lines.getFirst();
        CpuTimes cpuTimes = parseCpuTimes(line);
        CpuInfo cpuInfo = new CpuInfo(-1, temperatures.getOrDefault(-1, 0D), 0, cpuTimes);
        cpuInfoMap.put(cpuInfo.getCore(), cpuInfo);

        // Core Jiffies
        for (int i = 0; i < numCpus; i++) {
            line = lines.get(i + 1);

            cpuTimes = parseCpuTimes(line);
            final double temperature = temperatures.getOrDefault(i, 0D);
            final int frequency = frequencies.getOrDefault(i, 0);

            cpuInfo = new CpuInfo(i, temperature, frequency, cpuTimes);
            cpuInfoMap.put(cpuInfo.getCore(), cpuInfo);
        }

        return new CpuInfos(cpuInfoMap);
    }

    @Override
    public CpuLoadAvg getCpuLoadAvg() {
        final List<String> lines = readContent("/proc/loadavg");
        final String line = lines.getFirst();

        // ArchLinux
        // 0.40 0.91 1.09 1/999 73841

        // String[] splits = line.split(SPACE_PATTERN.pattern());
        final String[] splits = SPACE_PATTERN.split(line);

        return new CpuLoadAvg(Double.parseDouble(splits[0]), Double.parseDouble(splits[1]), Double.parseDouble(splits[2]));
    }

    // @Override
    // public Map<String, UsageInfo> getFilesystems() {
    //     final Map<String, UsageInfo> map = new HashMap<>();
    //
    //     final List<String> lines = readContent(processBuilderDf);
    //
    //     for (final String line : lines) {
    //         if (line.contains("/dev/md1") || line.contains("/tmp")) {
    //             final String[] splits = SPACE_PATTERN.split(line);
    //             final String path = splits[5];
    //             final long total = Long.parseLong(splits[1]);
    //             final long used = Long.parseLong(splits[2]);
    //             final long free = Long.parseLong(splits[3]);
    //
    //             map.put(path, new UsageInfo(path, total, used, free));
    //         }
    //     }
    //
    //     return map;
    // }

    // @Override
    // public HostInfo getHostInfo() {
    //     final List<String> lines = readContent(processBuilderUname);
    //     final String line = lines.getFirst();
    //
    //     // ArchLinux
    //     // Linux mainah 5.9.11-arch2-1 #1 SMP PREEMPT Sat, 28 Nov 2020 02:07:22 +0000 x86_64 GNU/Linux
    //
    //     // String[] splits = line.split(SPACE_PATTERN.pattern());
    //     final String[] splits = SPACE_PATTERN.split(line);
    //
    //     return = new HostInfo(splits[1], splits[2], splits[12] + " " + splits[13]);
    // }

    @Override
    public MusicInfo getMusicInfo() {
        List<String> lines = readContent(processBuilderPlayerCtlMetaData);
        // String output = lines.stream().collect(Collectors.joining("\n"));

        String artist = null;
        String album = null;
        String title = null;
        int length = 0;
        final int position;
        int bitRate = 0;
        URI imageUri = null;

        for (final String line : lines) {
            if (line.contains("xesam:artist ")) {
                final String[] splits = SPACE_PATTERN.split(line, 3);
                artist = splits[2];
            }
            else if (line.contains("xesam:album ")) {
                final String[] splits = SPACE_PATTERN.split(line, 3);
                album = splits[2];
            }
            else if (line.contains("xesam:title ")) {
                final String[] splits = SPACE_PATTERN.split(line, 3);
                title = splits[2];
            }
            else if (line.contains("mpris:length ")) {
                final String[] splits = SPACE_PATTERN.split(line, 3);
                length = (int) (Long.parseLong(splits[2]) / 1_000_000L); // Nano-Sekunden -> Sekunden
            }
            else if (line.contains("bitrate")) {
                final String[] splits = SPACE_PATTERN.split(line, 3);
                bitRate = Integer.parseInt(splits[2]);
            }
            else if (line.contains("mpris:artUrl")) {
                final String[] splits = SPACE_PATTERN.split(line, 3);
                imageUri = URI.create(splits[2]);
            }
        }

        lines = readContent(processBuilderPlayerCtlPosition);
        position = Double.valueOf(lines.getFirst()).intValue();

        return new MusicInfo(artist, album, title, length, position, bitRate, imageUri);
    }

    @Override
    public NetworkInfos getNetworkInfos() {
        // ip -s -4 addr
        final List<String> ipLines = new ArrayList<>(readContent(processBuilderNetworkIf));

        // Separate Interfaces.
        final Map<Integer, List<String>> map = new HashMap<>();
        int n = 0;

        for (final String line : ipLines) {
            if (line.contains(": <")) {
                n++;
            }

            if (n > 0) {
                map.computeIfAbsent(n, key -> new ArrayList<>()).add(line);
            }
        }

        final Map<String, NetworkInfo> networkInfoMap = new HashMap<>();

        for (final List<String> ifLines : map.values()) {
            String interfaceName = null;
            String ip = null;
            long bytesReceived = 0L;
            long bytesTransmitted = 0L;

            do {
                String line = ifLines.removeFirst().strip();

                if (line.contains(": <")) {
                    // Interface Name
                    final int index = line.indexOf(':');
                    interfaceName = line.substring(index + 1, line.indexOf(":", index + 1)).strip();
                }
                else if (line.startsWith("inet ")) {
                    // IP
                    final String[] splits = SPACE_PATTERN.split(line);
                    ip = splits[1];
                }
                else if (line.startsWith("RX:")) {
                    // Bytes Received
                    line = ifLines.removeFirst().strip();
                    final String[] splits = SPACE_PATTERN.split(line);
                    bytesReceived = Long.parseLong(splits[0]);
                }
                else if (line.startsWith("TX:")) {
                    // Bytes Transmitted
                    line = ifLines.removeFirst().strip();
                    final String[] splits = SPACE_PATTERN.split(line);
                    bytesTransmitted = Long.parseLong(splits[0]);
                }
            }
            while (!ifLines.isEmpty());

            if (interfaceName != null && !interfaceName.isEmpty()) {
                final NetworkInfo networkInfo = new NetworkInfo(interfaceName, ip, bytesReceived, bytesTransmitted);
                networkInfoMap.put(interfaceName, networkInfo);
            }
        }

        // Protokoll Infos.
        // nstat -a
        final List<String> nStatLines = readContent(processBuilderNstat);

        long icmpIn = 0;
        long icmpOut = 0;
        long ipIn = 0;
        long ipOut = 0;
        long tcpIn = 0;
        long tcpOut = 0;
        long udpIn = 0;
        long udpOut = 0;

        for (String line : nStatLines) {
            line = line.strip();

            if (line.contains("TcpInSegs")) {
                final String[] splits = SPACE_PATTERN.split(line);
                tcpIn = Long.parseLong(splits[1]);
            }
            else if (line.contains("TcpOutSegs")) {
                final String[] splits = SPACE_PATTERN.split(line);
                tcpOut = Long.parseLong(splits[1]);
            }
            else if (line.contains("UdpInDatagrams")) {
                final String[] splits = SPACE_PATTERN.split(line);
                udpIn = Long.parseLong(splits[1]);
            }
            else if (line.contains("UdpOutDatagrams")) {
                final String[] splits = SPACE_PATTERN.split(line);
                udpOut = Long.parseLong(splits[1]);
            }
            else if (line.contains("IpInReceives")) {
                final String[] splits = SPACE_PATTERN.split(line);
                ipIn = Long.parseLong(splits[1]);
            }
            else if (line.contains("IpOutRequests")) {
                final String[] splits = SPACE_PATTERN.split(line);
                ipOut = Long.parseLong(splits[1]);
            }
            else if (line.contains("Icmp6InMsgs")) {
                // IcmpInEchos
                final String[] splits = SPACE_PATTERN.split(line);
                icmpIn = Long.parseLong(splits[1]);
            }
            else if (line.contains("Icmp6OutMsgs")) {
                // IcmpOutEchoReps
                final String[] splits = SPACE_PATTERN.split(line);
                icmpOut = Long.parseLong(splits[1]);
            }
        }

        final NetworkProtocolInfo protocolInfo = new NetworkProtocolInfo(icmpIn, icmpOut, ipIn, ipOut, tcpIn, tcpOut, udpIn, udpOut);

        return new NetworkInfos(networkInfoMap, protocolInfo);
    }

    @Override
    public ProcessInfos getProcessInfos(final double uptimeInSeconds, final long totalSystemMemory) {
        return getProcessInfosByTop();
        // return getProcessInfosByProc(uptimeInSeconds, totalSystemMemory);
    }

    /**
     * /proc/meminfo
     */
    @Override
    public Map<String, UsageInfo> getRamAndSwap() {
        final Map<String, UsageInfo> map = super.getRamAndSwap();

        final List<String> lines = readContent(processBuilderFree);

        for (int i = 0; i < lines.size(); i++) {
            if (i == 1) {
                // Speicher
                final String line = lines.get(i).replace(":", ": ");
                final String[] splits = SPACE_PATTERN.split(line, -1);
                final long size = Long.parseLong(splits[1]);
                final long used = Long.parseLong(splits[2]);
                final long free = Long.parseLong(splits[3]);

                map.put("RAM", new UsageInfo("RAM", size, used, free));
            }
            // else if (i == 2) {
            //     // Swap
            //     final String line = lines.get(i).replace(":", ": ");
            //     final String[] splits = SPACE_PATTERN.split(line);
            //     final long size = Long.parseLong(splits[1]);
            //     final long used = Long.parseLong(splits[2]);
            //     final long free = Long.parseLong(splits[3]);
            //
            //     map.put("SWAP", new UsageInfo("SWAP", size, used, free));
            // }
        }

        return map;
    }

    @Override
    public Map<String, TemperatureInfo> getTemperatures() {
        final Map<String, TemperatureInfo> map = new HashMap<>();

        List<String> lines = readContent(processBuilderSmartCtl);

        for (final String line : lines) {
            if (line.startsWith("Temperature Sensor 2:")) {
                final String[] splits = SPACE_PATTERN.split(line);
                final double temperature = Double.parseDouble(splits[3]);

                map.put("/dev/nvme0n1", new TemperatureInfo("/dev/nvme0n1", temperature));
            }
        }

        lines = readContent(processBuilderNvidiaSmi);
        final String line = lines.getFirst();

        final String[] splits = SPACE_PATTERN.split(line);

        final double temperature = Double.parseDouble(splits[0].replace(",", ""));
        final double power = Double.parseDouble(splits[1].replace(",", ""));
        final int fanSpeed = Integer.parseInt(splits[2].replace(",", ""));
        final int usage = Integer.parseInt(splits[3].replace(",", ""));

        map.put("GPU", new GpuInfo(temperature, power, fanSpeed, usage));

        return map;
    }

    @Override
    public int getUpdates() {
        final long updates = readContent(processBuilderCheckUpdates).size();

        return (int) updates;
    }

    @Override
    public double getUptimeInSeconds() {
        final List<String> lines = readContent("/proc/uptime");
        final String line = lines.getFirst();

        // ArchLinux
        // 1147.04 8069.99

        final String[] splits = SPACE_PATTERN.split(line);

        return Double.parseDouble(splits[0]);
    }

    ProcessInfos getProcessInfosByProc(final double uptimeInSeconds, final long totalSystemMemory) {
        final String[] pids = new File("/proc").list(PROCESS_DIRECTORY_FILTER);

        final List<ProcessInfo> infos = new ArrayList<>(pids.length);

        for (final String pid : pids) {
            // /proc/4543/stat
            // 4543 (cinnamon) S 4231 3355 3355 0 -1 4194304 159763 53230 432 4977 11873 3096 1461 181 20 0 12 0 3831 4350136320 79932 18446744073709551615
            // 94314044076032 94314044078533 140729316610576 0 0 0 0 16781312 82952 0 0 0 17 0 0 0 833 0 0 94314044087280 94314044088448 94314055774208
            // 140729316617080 140729316617099 140729316617099 140729316618214 0
            final List<String> stat = readContent(String.format("/proc/%s/stat", pid));
            final List<String> cmdLine = readContent(String.format("/proc/%s/cmdline", pid));
            final List<String> status = readContent(String.format("/proc/%s/status", pid));

            if (stat.isEmpty() || cmdLine.isEmpty() || status.isEmpty()) {
                // Prozess existiert nicht mehr.
                continue;
            }

            final String lineStat = stat.getFirst();

            final String[] splitsStat = SPACE_PATTERN.split(lineStat);

            // String pid = splits[0];
            final String state = splitsStat[2];
            final int utimeJiffie = Integer.parseInt(splitsStat[13]); // CPU time spent in user code, measured in clock ticks.
            final int stimeJiffie = Integer.parseInt(splitsStat[14]); // CPU time spent in kernel code, measured in clock ticks.
            final int cutimeJiffie = Integer.parseInt(splitsStat[15]); // Waited-for children's CPU time spent in user code in clock ticks.
            final int cstimeJiffie = Integer.parseInt(splitsStat[13]); // Waited-for children's CPU time spent in kernel code in clock ticks.
            final int starttime = Integer.parseInt(splitsStat[21]); // Waited-for children's CPU time spent in kernel code in clock ticks.

            double totalTimeJiffie = (double) utimeJiffie + stimeJiffie;

            // Inklusive Child-Processes.
            totalTimeJiffie += cutimeJiffie + cstimeJiffie;

            final double seconds = uptimeInSeconds - JConkyUtils.jiffieToSeconds(starttime);
            final double cpuUsage = JConkyUtils.jiffieToSeconds(totalTimeJiffie) / seconds;

            String command;

            if (!cmdLine.isEmpty()) {
                command = cmdLine.getFirst();
            }
            else {
                command = splitsStat[1];
            }

            command = command.replace("(", "").replace(")", "").replace("\\r", "").replace("\\n", "");

            final String statusOutput = String.join(System.lineSeparator(), status);

            Matcher matcher = STATUS_NAME_PATTERN.matcher(statusOutput);
            final String name;

            if (matcher.find()) {
                name = matcher.group(1);
            }
            else {
                name = command;
            }

            matcher = STATUS_VM_RSS_PATTERN.matcher(statusOutput);
            long residentBytes = 0L;

            if (matcher.find()) {
                residentBytes = Long.parseLong(matcher.group(1));
            }

            // matcher = STATUS_VM_SIZE_MATCHER.matcher(status);
            // long totalBytes = 0L;
            //
            // if (matcher.find()) {
            // totalBytes = Long.parseLong(matcher.group(1));
            // }

            matcher = STATUS_UID_PATTERN.matcher(statusOutput);
            matcher.find();
            final String uid = matcher.group(1);

            final ProcessInfo processInfo = new ProcessInfo(Integer.parseInt(pid), state, name, uid, cpuUsage, (double) residentBytes / totalSystemMemory);
            infos.add(processInfo);
        }

        return new ProcessInfos(infos);
    }

    ProcessInfos getProcessInfosByTop() {
        final List<String> lines = readContent(processBuilderTop);
        // final String output = lines.stream().collect(Collectors.joining("\n"));

        // GiB Spch: 15,6 total, 12,4 free, 2,0 used, 1,1 buff/cache
        // GiB Swap: 14,4 total, 14,4 free, 0,0 used. 13,2 avail Spch

        // Bis zur ProzessListe gehen.
        int startIndex = 0;

        for (final String line : lines) {
            startIndex++;

            if (line.strip().startsWith("PID USER")) {
                break;
            }
        }

        final List<ProcessInfo> infos = lines.stream()
                .skip(startIndex)
                .filter(line -> line != null && !line.isBlank())
                .map(line -> SPACE_PATTERN.split(line.strip()))
                .map(splits -> {
                    final int pid = Integer.parseInt(splits[0]);
                    final String owner = splits[1];
                    final double cpuUsage = Double.parseDouble(splits[8].replace(",", "."));
                    final double memoryUsage = Double.parseDouble(splits[9].replace(",", "."));
                    final String state = splits[7];
                    final String name = splits[11];

                    // jConky itself.

                    if (getMyPid() == pid) {
                        // jConky itself.
                        return null;
                    }

                    if ("top".equals(name)) {
                        // top itself.
                        return null;
                    }

                    return new ProcessInfo(pid, state, name, owner, cpuUsage / 100D, memoryUsage / 100D);
                })
                .filter(Objects::nonNull)
                .toList();

        return new ProcessInfos(infos);
    }

    /**
     * <pre>{@code
     * /sys/devices/system/cpu/cpu<N>/cpufreq/scaling_cur_freq
     * }</pre>
     */
    private Map<Integer, Integer> getCpuFrequencies(final int numCpus) {
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

    private Map<Integer, Double> getCpuTemperatures() {
        final Map<Integer, Double> temperatures = new HashMap<>();

        final String output = String.join(System.lineSeparator(), readContent(processBuilderSensors));

        // Package
        final Matcher matcher = SENSORS_PACKAGE_PATTERN.matcher(output);

        if (matcher.find()) {
            final String line = matcher.group();

            final String[] splits = SPACE_PATTERN.split(line);

            String temperatureString = splits[1];
            temperatureString = temperatureString.replace("+", "").replace("°C", "");
            final double temperature = Double.parseDouble(temperatureString);

            temperatures.put(-1, temperature);
        }

        // Bei AMD gib's keine Temperatur pro Core.
        //
        // final Matcher matcher = SENSORS_CORE_PATTERN.matcher(output);
        //
        // while (matcher.find()) {
        // final String line = matcher.group();
        //
        // final String[] splits = SPACE_PATTERN.split(line);
        //
        // String coreString = splits[1];
        // coreString = coreString.replace(":", "");
        // final int core = Integer.parseInt(coreString);
        //
        // String temperatureString = splits[2];
        // temperatureString = temperatureString.replace("+", "").replace("°C", "");
        // final double temperature = Double.parseDouble(temperatureString);
        //
        // temperatures.put(core, temperature);
        // }

        return temperatures;
    }
}
