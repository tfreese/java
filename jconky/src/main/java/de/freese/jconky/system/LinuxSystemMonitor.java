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

import de.freese.jconky.model.GpuInfo;
import de.freese.jconky.model.MusicInfo;
import de.freese.jconky.model.ProcessInfo;
import de.freese.jconky.model.ProcessInfos;
import de.freese.jconky.model.TemperatureInfo;
import de.freese.jconky.util.JConkyUtils;

/**
 * @author Thomas Freese
 * @since 01.12.2020
 */
public class LinuxSystemMonitor extends AbstractSystemMonitor {

    private static final Pattern APROC_DIR_PATTERN = Pattern.compile("([\\d]*)");

    private static final FilenameFilter PROCESS_DIRECTORY_FILTER = (dir, name) -> {
        final File fileToTest = new File(dir, name);

        return fileToTest.isDirectory() && APROC_DIR_PATTERN.matcher(name).matches();
    };
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

    private final ProcessBuilder processBuilderCheckUpdates;
    private final ProcessBuilder processBuilderNvidiaSmi;
    private final ProcessBuilder processBuilderPlayerCtlMetaData;
    private final ProcessBuilder processBuilderPlayerCtlPosition;
    private final ProcessBuilder processBuilderSmartCtl;
    private final ProcessBuilder processBuilderTop;
    // private final ProcessBuilder processBuilderUname;

    public LinuxSystemMonitor() {
        super();

        processBuilderTop = new ProcessBuilder().command("/bin/sh", "-c", "top -b -n 1");

        processBuilderCheckUpdates = new ProcessBuilder("/bin/sh", "-c", "checkupdates");
        processBuilderPlayerCtlMetaData = new ProcessBuilder("/bin/sh", "-c", "playerctl -s metadata");
        processBuilderPlayerCtlPosition = new ProcessBuilder("/bin/sh", "-c", "playerctl -s position");
        processBuilderSmartCtl = new ProcessBuilder("/bin/sh", "-c", "sudo smartctl -A /dev/nvme0n1");
        processBuilderNvidiaSmi = new ProcessBuilder("/bin/sh", "-c", "nvidia-smi --format=csv,noheader,nounits --query-gpu=temperature.gpu,power.draw,fan.speed,utilization.gpu");
    }

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
    public ProcessInfos getProcessInfos(final double uptimeInSeconds, final long totalSystemMemory) {
        return getProcessInfosByTop();
        // return getProcessInfosByProc(uptimeInSeconds, totalSystemMemory);
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
}
