// Created: 01.12.2020
package de.freese.jconky.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.freese.jconky.model.CpuInfo;
import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.CpuTimes;
import de.freese.jconky.model.GpuInfo;
import de.freese.jconky.model.HostInfo;
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
 */
public class LinuxSystemMonitor extends AbstractSystemMonitor {
    /**
     * /proc/cpuinfo: processor\\s+:\\s+(\\d+)
     */
    static final Pattern CPUINFO_NUM_CPU_PATTERN = Pattern.compile("processor\\s+:\\s+(\\d+)", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    /**
     * /proc/stat: cpu\\s+(.*)
     */
    static final Pattern CPU_JIFFIES_PATTERN = Pattern.compile("cpu\\s+(.*)", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    /**
     * Bei AMD gib's keine Temperatur pro Core.<br>
     * sensors: Core\\s{1}\\d+:.*
     */
    static final Pattern SENSORS_CORE_PATTERN = Pattern.compile("Core\\s{1}\\d+:.*", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
    /**
     * /proc/stat: cpu\\d+
     */
    protected static final Pattern STAT_NUM_CPU_PATTERN = Pattern.compile("cpu\\d+", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);

    private static final Pattern APROC_DIR_PATTERN = Pattern.compile("([\\d]*)");

    private static final FilenameFilter PROCESS_DIRECTORY_FILTER = (dir, name) -> {
        File fileToTest = new File(dir, name);

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

    private final ProcessBuilder processBuilderCheckUpdates;

    private final ProcessBuilder processBuilderDf;

    private final ProcessBuilder processBuilderFree;

    private final ProcessBuilder processBuilderHddtemp;

    private final ProcessBuilder processBuilderIfconfig;

    private final ProcessBuilder processBuilderNetstat;

    private final ProcessBuilder processBuilderNvidiaSmi;

    private final ProcessBuilder processBuilderPlayerctlMetaData;

    private final ProcessBuilder processBuilderPlayerctlPosition;

    private final ProcessBuilder processBuilderSensors;

    private final ProcessBuilder processBuilderSmartctl;

    private final ProcessBuilder processBuilderTop;

    private final ProcessBuilder processBuilderUname;

    public LinuxSystemMonitor() {
        super();

        // .redirectErrorStream(true); // Gibt Fehler auf dem InputStream aus.
        this.processBuilderUname = new ProcessBuilder().command("/bin/sh", "-c", "uname --all");

        this.processBuilderSensors = new ProcessBuilder().command("/bin/sh", "-c", "sensors");

        // -u tommy
        this.processBuilderTop = new ProcessBuilder().command("/bin/sh", "-c", "top -b -n 1");

        this.processBuilderIfconfig = new ProcessBuilder().command("/bin/sh", "-c", "ifconfig");
        this.processBuilderNetstat = new ProcessBuilder().command("/bin/sh", "-c", "netstat --statistics ");
        this.processBuilderDf = new ProcessBuilder().command("/bin/sh", "-c", "df --block-size=1K");
        this.processBuilderFree = new ProcessBuilder().command("/bin/sh", "-c", "free --bytes");
        this.processBuilderCheckUpdates = new ProcessBuilder().command("/bin/sh", "-c", "checkupdates");
        this.processBuilderPlayerctlMetaData = new ProcessBuilder().command("/bin/sh", "-c", "playerctl -s metadata");
        this.processBuilderPlayerctlPosition = new ProcessBuilder().command("/bin/sh", "-c", "playerctl -s position");
        this.processBuilderHddtemp = new ProcessBuilder().command("/bin/sh", "-c", "sudo hddtemp /dev/sda /dev/sdb");
        this.processBuilderSmartctl = new ProcessBuilder().command("/bin/sh", "-c", "sudo smartctl -A /dev/nvme0n1");
        this.processBuilderNvidiaSmi = new ProcessBuilder().command("/bin/sh", "-c", "nvidia-smi --format=csv,noheader,nounits --query-gpu=temperature.gpu,power.draw,fan.speed,utilization.gpu");
    }

    @Override
    public CpuInfos getCpuInfos() {
        // String output = readContent("/proc/cpuinfo").stream().collect(Collectors.joining("\n"));
        //
        // int numCpus = 0;
        //
        // Matcher matcher = CPUINFO_NUM_CPU_PATTERN.matcher(output);
        //
        // while (matcher.find())
        // {
        // numCpus++;
        // }

        List<String> lines = readContent("/proc/stat");
        // String output = lines.stream().collect(Collectors.joining("\n"));
        //
        // int numCpus = 0;
        //
        // Matcher matcher = STAT_NUM_CPU_PATTERN.matcher(output);
        //
        // while (matcher.find())
        // {
        // numCpus++;
        // }

        int numCpus = Runtime.getRuntime().availableProcessors();

        // Temperaturen
        Map<Integer, Double> temperatures = getCpuTemperatures();

        // Frequenzen
        Map<Integer, Integer> frequencies = getCpuFrequencies(numCpus);

        Map<Integer, CpuInfo> cpuInfoMap = new HashMap<>();

        // Total Jiffies
        String line = lines.get(0);
        CpuTimes cpuTimes = parseCpuTimes(line);
        CpuInfo cpuInfo = new CpuInfo(-1, temperatures.getOrDefault(-1, 0D), 0, cpuTimes);
        cpuInfoMap.put(cpuInfo.getCore(), cpuInfo);

        // Core Jiffies
        for (int i = 0; i < numCpus; i++) {
            line = lines.get(i + 1);

            cpuTimes = parseCpuTimes(line);
            double temperature = temperatures.getOrDefault(i, 0D);
            int frequency = frequencies.getOrDefault(i, 0);

            cpuInfo = new CpuInfo(i, temperature, frequency, cpuTimes);
            cpuInfoMap.put(cpuInfo.getCore(), cpuInfo);
        }

        CpuInfos cpuInfos = new CpuInfos(cpuInfoMap);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(cpuInfos.get(-1).toString());
        }

        return cpuInfos;
    }

    @Override
    public CpuLoadAvg getCpuLoadAvg() {
        List<String> lines = readContent("/proc/loadavg");
        String line = lines.get(0);

        // ArchLinux
        // 0.40 0.91 1.09 1/999 73841

        // String[] splits = line.split(SPACE_PATTERN.pattern());
        String[] splits = SPACE_PATTERN.split(line);

        CpuLoadAvg loadAvg = new CpuLoadAvg(Double.parseDouble(splits[0]), Double.parseDouble(splits[1]), Double.parseDouble(splits[2]));

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(loadAvg.toString());
        }

        return loadAvg;
    }

    @Override
    public String getExternalIp() {
        String externalIp = "";

        try {
            URL url = URI.create("https://ifconfig.me").toURL();
            URLConnection connection = url.openConnection();
            // connection.connect();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                externalIp = br.readLine();
            }
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        getLogger().debug("externalIp = {}", externalIp);

        return externalIp;
    }

    @Override
    public Map<String, UsageInfo> getFilesystems() {
        Map<String, UsageInfo> map = new HashMap<>();

        List<String> lines = readContent(this.processBuilderDf);

        for (String line : lines) {
            if (line.contains("vgdesktop-root") || line.contains("/tmp")) {
                String[] splits = SPACE_PATTERN.split(line);
                String path = splits[5];
                long size = Long.parseLong(splits[1]);
                long used = Long.parseLong(splits[2]);

                map.put(path, new UsageInfo(path, size * 1024L, used * 1024L));
            }
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(map.toString());
        }

        return map;
    }

    @Override
    public HostInfo getHostInfo() {
        List<String> lines = readContent(this.processBuilderUname);
        String line = lines.get(0);

        // ArchLinux
        // Linux mainah 5.9.11-arch2-1 #1 SMP PREEMPT Sat, 28 Nov 2020 02:07:22 +0000 x86_64 GNU/Linux

        // String[] splits = line.split(SPACE_PATTERN.pattern());
        String[] splits = SPACE_PATTERN.split(line);

        HostInfo hostInfo = new HostInfo(splits[1], splits[2], splits[12] + " " + splits[13]);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(hostInfo.toString());
        }

        return hostInfo;
    }

    @Override
    public MusicInfo getMusicInfo() {
        List<String> lines = readContent(this.processBuilderPlayerctlMetaData);
        // String output = lines.stream().collect(Collectors.joining("\n"));

        String artist = null;
        String album = null;
        String title = null;
        int length = 0;
        int position;
        int bitRate = 0;
        URI imageUri = null;

        for (String line : lines) {
            if (line.contains("xesam:artist ")) {
                String[] splits = SPACE_PATTERN.split(line, 3);
                artist = splits[2];
            }
            else if (line.contains("xesam:album ")) {
                String[] splits = SPACE_PATTERN.split(line, 3);
                album = splits[2];
            }
            else if (line.contains("xesam:title ")) {
                String[] splits = SPACE_PATTERN.split(line, 3);
                title = splits[2];
            }
            else if (line.contains("mpris:length ")) {
                String[] splits = SPACE_PATTERN.split(line, 3);
                length = (int) (Long.parseLong(splits[2]) / 1_000_000L); // Nano-Sekunden -> Sekunden
            }
            else if (line.contains("bitrate")) {
                String[] splits = SPACE_PATTERN.split(line, 3);
                bitRate = Integer.parseInt(splits[2]);
            }
            else if (line.contains("mpris:artUrl")) {
                String[] splits = SPACE_PATTERN.split(line, 3);
                imageUri = URI.create(splits[2]);
            }
        }

        lines = readContent(this.processBuilderPlayerctlPosition);
        position = Double.valueOf(lines.get(0)).intValue();

        MusicInfo musicInfo = new MusicInfo(artist, album, title, length, position, bitRate, imageUri);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(musicInfo.toString());
        }

        return musicInfo;
    }

    @Override
    public NetworkInfos getNetworkInfos() {
        // ifconfig
        // cat /sys/class/net/
        // cat /proc/net/dev
        List<String> lines = readContent(this.processBuilderIfconfig);

        // Trennung der Interfaces durch leere Zeile.
        Map<Integer, List<String>> map = new HashMap<>();
        int n = 0;

        for (String line : lines) {
            if (line.isBlank()) {
                n++;
                continue;
            }

            map.computeIfAbsent(n, key -> new ArrayList<>()).add(line);
        }

        Map<String, NetworkInfo> networkInfoMap = new HashMap<>();

        for (List<String> ifLines : map.values()) {
            String interfaceName = null;
            String ip = null;
            long bytesReceived = 0L;
            long bytesTransmitted = 0L;

            for (int i = 0; i < ifLines.size(); i++) {
                String line = ifLines.get(i).strip();

                if (i == 0) {
                    // Interface Name
                    int index = line.indexOf(':');
                    interfaceName = line.substring(0, index);
                }
                else if (line.startsWith("inet ")) {
                    // IP
                    String[] splits = SPACE_PATTERN.split(line);
                    ip = splits[1];
                }
                else if (line.startsWith("RX packets")) {
                    // Bytes Received
                    String[] splits = SPACE_PATTERN.split(line);
                    bytesReceived = Long.parseLong(splits[4]);
                }
                else if (line.startsWith("TX packets")) {
                    // Bytes Transmitted
                    String[] splits = SPACE_PATTERN.split(line);
                    bytesTransmitted = Long.parseLong(splits[4]);
                }
            }

            NetworkInfo networkInfo = new NetworkInfo(interfaceName, ip, bytesReceived, bytesTransmitted);
            networkInfoMap.put(interfaceName, networkInfo);
        }

        // Protokoll infos
        // ss -s
        // ss -l
        // ss -t -a
        // ss -t -s
        // netstat -natp
        // netstat -nat
        // netstat -natu | grep 'ESTABLISHED'
        // netstat -s
        lines = readContent(this.processBuilderNetstat);
        // String output = lines.stream().collect(Collectors.joining("\n"));
        // Pattern patternIpIn =
        // Pattern.compile("\\d+\\s+(total packets received|Pakete insgesamt empfangen)", Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
        // Matcher matcher = patternIpIn.matcher(output);
        // if (matcher.find())
        // {
        // System.out.println(matcher.group());
        // }
        long icmpIn = 0;
        long icmpOut = 0;
        long ipIn = 0;
        long ipOut = 0;
        int tcpConnections = 0;
        long tcpIn = 0;
        long tcpOut = 0;
        long udpIn = 0;
        long udpOut = 0;

        for (String line : lines) {
            line = line.strip();

            if (line.contains("total packets received") || line.contains("Pakete insgesamt empfangen")) {
                String[] splits = SPACE_PATTERN.split(line);
                ipIn = Long.parseLong(splits[0]);
            }
            else if (line.contains("requests sent out") || line.contains("eingehende Pakete ausgeliefert")) {
                String[] splits = SPACE_PATTERN.split(line);
                ipOut = Long.parseLong(splits[0]);
            }
            else if (line.contains("ICMP messages received") || line.contains("ICMP-Meldungen empfangen")) {
                String[] splits = SPACE_PATTERN.split(line);
                icmpIn = Long.parseLong(splits[0]);
            }
            else if (line.contains("ICMP messages sent") || line.contains("ICMP Nachrichten gesendet")) {
                String[] splits = SPACE_PATTERN.split(line);
                icmpOut = Long.parseLong(splits[0]);
            }
            else if (line.contains("connections established") || line.contains("Verbindungen aufgebaut")) {
                String[] splits = SPACE_PATTERN.split(line);
                tcpConnections = Integer.parseInt(splits[0]);
            }
            else if ((line.contains("segments received") || line.contains("Segmente empfangen")) && (tcpIn == 0)) {
                // 45825 segments received
                // 0 bad segments received
                String[] splits = SPACE_PATTERN.split(line);
                tcpIn = Long.parseLong(splits[0]);
            }
            else if (line.contains("segments sent out") || line.contains("Segmente ausgesendet")) {
                String[] splits = SPACE_PATTERN.split(line);
                tcpOut = Long.parseLong(splits[0]);
            }
            else if (line.contains("packets received") || line.contains("Pakete empfangen")) {
                String[] splits = SPACE_PATTERN.split(line);
                udpIn = Long.parseLong(splits[0]);
            }
            else if (line.contains("packets sent") || line.contains("Pakete gesendet")) {
                String[] splits = SPACE_PATTERN.split(line);
                udpOut = Long.parseLong(splits[0]);
            }
        }

        NetworkProtocolInfo protocolInfo = new NetworkProtocolInfo(icmpIn, icmpOut, ipIn, ipOut, tcpConnections, tcpIn, tcpOut, udpIn, udpOut);
        NetworkInfos networkInfos = new NetworkInfos(networkInfoMap, protocolInfo);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(networkInfos.toString());
        }

        return networkInfos;
    }

    @Override
    public ProcessInfos getProcessInfos(final double uptimeInSeconds, final long totalSystemMemory) {
        ProcessInfos processInfos = getProcessInfosByTop();
        // ProcessInfos processInfos = getProcessInfosByProc(uptimeInSeconds, totalSystemMemory);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(processInfos.toString());
        }

        return processInfos;
    }

    @Override
    public Map<String, UsageInfo> getRamAndSwap() {
        // /proc/meminfo
        Map<String, UsageInfo> map = new HashMap<>();

        List<String> lines = readContent(this.processBuilderFree);

        for (int i = 0; i < lines.size(); i++) {
            if (i == 1) {
                // Speicher
                String line = lines.get(i).replace(":", ": ");
                String[] splits = SPACE_PATTERN.split(line);
                String path = "RAM";
                long size = Long.parseLong(splits[1]);
                long used = Long.parseLong(splits[2]);

                map.put(path, new UsageInfo(path, size, used));
            }
            else if (i == 2) {
                // Swap
                String line = lines.get(i).replace(":", ": ");
                String[] splits = SPACE_PATTERN.split(line);
                String path = "SWAP";
                long size = Long.parseLong(splits[1]);
                long used = Long.parseLong(splits[2]);

                map.put(path, new UsageInfo(path, size, used));
            }
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(map.toString());
        }

        return map;
    }

    @Override
    public Map<String, TemperatureInfo> getTemperatures() {
        Map<String, TemperatureInfo> map = new HashMap<>();

        List<String> lines = readContent(this.processBuilderHddtemp);
        // String output = lines.stream().collect(Collectors.joining("\n"))

        for (String line : lines) {
            String[] splits = SPACE_PATTERN.split(line);
            String device = splits[0].replace(":", "");
            double temperature = Double.parseDouble(splits[splits.length - 1].replace("°C", ""));

            map.put(device, new TemperatureInfo(device, temperature));
        }

        lines = readContent(this.processBuilderSmartctl);

        for (String line : lines) {
            if (line.startsWith("Temperature Sensor 2:")) {
                String[] splits = SPACE_PATTERN.split(line);
                double temperature = Double.parseDouble(splits[3]);

                map.put("/dev/nvme0n1", new TemperatureInfo("/dev/nvme0n1", temperature));
            }
        }

        lines = readContent(this.processBuilderNvidiaSmi);
        String line = lines.get(0);

        String[] splits = SPACE_PATTERN.split(line);

        double temperature = Double.parseDouble(splits[0].replace(",", ""));
        double power = Double.parseDouble(splits[1].replace(",", ""));
        int fanSpeed = Integer.parseInt(splits[2].replace(",", ""));
        int usage = Integer.parseInt(splits[3].replace(",", ""));

        map.put("GPU", new GpuInfo(temperature, power, fanSpeed, usage));

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(map.toString());
        }

        return map;
    }

    @Override
    public int getUpdates() {
        long updates = readContent(this.processBuilderCheckUpdates).size();

        getLogger().debug("updates = {}", updates);

        return (int) updates;
    }

    @Override
    public double getUptimeInSeconds() {
        List<String> lines = readContent("/proc/uptime");
        String line = lines.get(0);

        // ArchLinux
        // 1147.04 8069.99

        String[] splits = SPACE_PATTERN.split(line);

        double uptimeInSeconds = Double.parseDouble(splits[0]);

        getLogger().debug("uptimeInSeconds = {}", uptimeInSeconds);

        return uptimeInSeconds;
    }

    ProcessInfos getProcessInfosByProc(final double uptimeInSeconds, final long totalSystemMemory) {
        String[] pids = new File("/proc").list(PROCESS_DIRECTORY_FILTER);

        List<ProcessInfo> infos = new ArrayList<>(pids.length);

        for (String pid : pids) {
            // /proc/4543/stat
            // 4543 (cinnamon) S 4231 3355 3355 0 -1 4194304 159763 53230 432 4977 11873 3096 1461 181 20 0 12 0 3831 4350136320 79932 18446744073709551615
            // 94314044076032 94314044078533 140729316610576 0 0 0 0 16781312 82952 0 0 0 17 0 0 0 833 0 0 94314044087280 94314044088448 94314055774208
            // 140729316617080 140729316617099 140729316617099 140729316618214 0
            List<String> stat = readContent(String.format("/proc/%s/stat", pid));
            List<String> cmdLine = readContent(String.format("/proc/%s/cmdline", pid));
            List<String> status = readContent(String.format("/proc/%s/status", pid));

            if (stat.isEmpty() || cmdLine.isEmpty() || status.isEmpty()) {
                // Prozess existiert nicht mehr.
                continue;
            }

            String lineStat = stat.get(0);

            String[] splitsStat = SPACE_PATTERN.split(lineStat);

            // String pid = splits[0];
            String state = splitsStat[2];
            int utimeJiffie = Integer.parseInt(splitsStat[13]); // CPU time spent in user code, measured in clock ticks.
            int stimeJiffie = Integer.parseInt(splitsStat[14]); // CPU time spent in kernel code, measured in clock ticks.
            int cutimeJiffie = Integer.parseInt(splitsStat[15]); // Waited-for children's CPU time spent in user code in clock ticks.
            int cstimeJiffie = Integer.parseInt(splitsStat[13]); // Waited-for children's CPU time spent in kernel code in clock ticks.
            int starttime = Integer.parseInt(splitsStat[21]); // Waited-for children's CPU time spent in kernel code in clock ticks.

            double totalTimeJiffie = (double) utimeJiffie + stimeJiffie;

            // Inklusive Child-Processes.
            totalTimeJiffie += cutimeJiffie + cstimeJiffie;

            double seconds = uptimeInSeconds - JConkyUtils.jiffieToSeconds(starttime);
            double cpuUsage = JConkyUtils.jiffieToSeconds(totalTimeJiffie) / seconds;

            String command = null;

            if (!cmdLine.isEmpty()) {
                command = cmdLine.get(0);
            }
            else {
                command = splitsStat[1];
            }

            command = command.replace("(", "").replace(")", "").replace("\\r", "").replace("\\n", "");

            String statusOutput = String.join("\n", status);

            Matcher matcher = STATUS_NAME_PATTERN.matcher(statusOutput);
            String name = null;

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
            // if (matcher.find())
            // {
            // totalBytes = Long.parseLong(matcher.group(1));
            // }

            matcher = STATUS_UID_PATTERN.matcher(statusOutput);
            matcher.find();
            String uid = matcher.group(1);
            String owner = uid;

            ProcessInfo processInfo = new ProcessInfo(Integer.parseInt(pid), state, name, owner, cpuUsage, (double) residentBytes / totalSystemMemory);
            infos.add(processInfo);

            // TODO /etc/passwd auslesen für UIDs.
        }

        return new ProcessInfos(infos);
    }

    ProcessInfos getProcessInfosByTop() {
        List<ProcessInfo> infos = new ArrayList<>(300);

        List<String> lines = readContent(this.processBuilderTop);
        // String output = lines.stream().collect(Collectors.joining("\n"));

        // GiB Spch: 15,6 total, 12,4 free, 2,0 used, 1,1 buff/cache
        // GiB Swap: 14,4 total, 14,4 free, 0,0 used. 13,2 avail Spch

        // Bis zur ProzessLise gehen.
        int startIndex = 0;

        for (String line : lines) {
            startIndex++;

            if (line.strip().startsWith("PID USER")) {
                break;
            }
        }

        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i);

            String[] splits = SPACE_PATTERN.split(line.strip());

            int pid = Integer.parseInt(splits[0]);
            String owner = splits[1];
            double cpuUsage = Double.parseDouble(splits[8].replace(",", "."));
            double memoryUsage = Double.parseDouble(splits[9].replace(",", "."));
            String state = splits[7];
            String name = splits[11];

            if (getMyPid() == pid) {
                // jConky wollen wir nicht.
                continue;
            }
            else if ("top".equals(name)) {
                // top wollen wir nicht.
                continue;
            }
            // else if ("java".equals(name))
            // {
            // // java wollen wir nicht.
            // continue;
            // }

            ProcessInfo processInfo = new ProcessInfo(pid, state, name, owner, cpuUsage / 100D, memoryUsage / 100D);
            infos.add(processInfo);
        }

        return new ProcessInfos(infos);
    }

    /**
     * /sys/devices/system/cpu/cpu<N>/cpufreq/scaling_cur_freq
     */
    private Map<Integer, Integer> getCpuFrequencies(final int numCpus) {
        Map<Integer, Integer> frequencies = new HashMap<>();

        for (int i = 0; i < numCpus; i++) {
            String file = String.format("/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq", i);
            List<String> lines = readContent(file);

            // Nur eine Zeile erwartet.
            String line = lines.get(0);

            int frequency = Integer.parseInt(line);

            frequencies.put(i, frequency);
        }

        return frequencies;
    }

    private Map<Integer, Double> getCpuTemperatures() {
        Map<Integer, Double> temperatures = new HashMap<>();

        String output = String.join("\n", readContent(this.processBuilderSensors));

        // Package
        Matcher matcher = SENSORS_PACKAGE_PATTERN.matcher(output);

        if (matcher.find()) {
            String line = matcher.group();

            String[] splits = SPACE_PATTERN.split(line);

            String temperatureString = splits[1];
            temperatureString = temperatureString.replace("+", "").replace("°C", "");
            double temperature = Double.parseDouble(temperatureString);

            temperatures.put(-1, temperature);
        }

        // Bei AMD gib's keine Temperatur pro Core.
        //
        // Matcher matcher = SENSORS_CORE_PATTERN.matcher(output);
        //
        // while (matcher.find())
        // {
        // String line = matcher.group();
        //
        // String[] splits = SPACE_PATTERN.split(line);
        //
        // String coreString = splits[1];
        // coreString = coreString.replace(":", "");
        // int core = Integer.parseInt(coreString);
        //
        // String temperatureString = splits[2];
        // temperatureString = temperatureString.replace("+", "").replace("°C", "");
        // double temperature = Double.parseDouble(temperatureString);
        //
        // temperatures.put(core, temperature);
        // }

        return temperatures;
    }

    private CpuTimes parseCpuTimes(final String line) {
        String[] splits = SPACE_PATTERN.split(line);

        long user = Long.parseLong(splits[1]);
        long nice = Long.parseLong(splits[2]);
        long system = Long.parseLong(splits[3]);
        long idle = Long.parseLong(splits[4]);
        long ioWait = Long.parseLong(splits[5]);
        long irq = Long.parseLong(splits[6]);
        long softIrq = Long.parseLong(splits[7]);
        long steal = Long.parseLong(splits[8]);
        long guest = Long.parseLong(splits[9]);
        long guestNice = Long.parseLong(splits[10]);

        return new CpuTimes(user, nice, system, idle, ioWait, irq, softIrq, steal, guest, guestNice);
    }
}
