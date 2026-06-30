// Created: 30.11.2020
package de.freese.jconky;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jconky.model.CpuTimes;

/**
 * @author Thomas Freese
 */
public final class SystemMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemMain.class);

    private static final com.sun.management.OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN =
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static CpuTimes prev = new CpuTimes();

    static void main() {
        try (ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2)) {
            // cpu(scheduledExecutorService);

            unixSockets(scheduledExecutorService);

            // Keep the Program running.
            System.in.read();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static void cpu(final ScheduledExecutorService scheduledExecutorService) {
        // cat /proc/stat
        // cpu 247721 450 70350 2534219 43469 8434 2372 0 0 0
        // cpu0 32043 74 7737 310311 11885 570 673 0 0 0
        // cpu1 26344 48 11872 312233 6875 5683 232 0 0 0
        // [...]
        // Relevant sind jeweils die ersten vier Zahlen, die für User, Nice, System und Idle stehen.
        // Zusammengerechnet geben sie im Beispiel 198.083, wovon der Idle-Wert 158.570 ausmacht, was etwa 80 % des Gesamtwerts entspricht.
        // Die effektive CPU-Auslastung seit Systemstart liegt also bei gerade 20 %.
        //
        // Effektive Auslastung: user nice system idle iowait irq softirq steal guest guest_nice
        //
        // PrevIdle = previdle + previowait
        // Idle = idle + iowait
        //
        // PrevNonIdle = prevuser + prevnice + prevsystem + previrq + prevsoftirq + prevsteal
        // NonIdle = user + nice + system + irq + softirq + steal
        //
        // PrevTotal = PrevIdle + PrevNonIdle
        // Total = Idle + NonIdle
        //
        // # differentiate: actual value minus the previous one
        // totald = Total - PrevTotal
        // idled = Idle - PrevIdle
        //
        // CPU_Percentage = (totald - idled)/totald

        scheduledExecutorService.scheduleWithFixedDelay(SystemMain::showCpuLoad, 1L, 1L, TimeUnit.SECONDS);
    }

    private static Optional<CpuTimes> getCpuTimes() {
        // Files.lines(path, cs)
        // Files.readAllLines(path, cs)

        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/stat", StandardCharsets.UTF_8))) {
            final String line = reader.readLine();

            // "[ ]" = "\\s+" = Whitespace: einer oder mehrere
            final String[] splits = line.split("\\s+");

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

            return Optional.of(new CpuTimes(user, nice, system, idle, ioWait, irq, softIrq, steal, guest, guestNice));
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return Optional.empty();
    }

    private static void showCpuLoad() {
        LOGGER.info("CPU-Load [%] - OperatingSystemMXBean: {}", OPERATING_SYSTEM_MX_BEAN.getCpuLoad() * 100D);

        final Optional<CpuTimes> cpuTimesOptional = getCpuTimes();

        cpuTimesOptional.ifPresent(cpuTimes -> {
            LOGGER.info("CPU-Load [%] - Vorgänger-Rechnung: {}", cpuTimes.getCpuUsage(prev) * 100D);
            prev = cpuTimes;
        });
    }

    private static void unixSockets(final ScheduledExecutorService scheduledExecutorService) throws IOException {
        // lsusb
        // lsusb -d 046d:c333
        // Search for Keyboard
        // /sys/devices/pci0000:00/0000:00:08.1/0000:0d:00.3/usb5/5-3/uevent
        // showkey -a
        // xev -event keyboard
        // l /dev/input/by-id/
        // /dev/input/by-id/usb-Logitech_Gaming_Keyboard_G610_108B356F3936-if01-event-kbd -> ../event5
        // evtest /dev/input/event5
        // final UnixDomainSocketAddress address = UnixDomainSocketAddress.of("/dev/bus/usb/005/003");

        // In the same User-Group 'input', but got still "No Access".
        final Path path = Path.of("/dev", "input", "event5");
        // final Path path = Path.of("/dev", "bus", "usb", "005", "003");
        // final Path path = Path.of("/tmp", "kdsingleapp-tommy-strawberry");
        // final Path path = Path.of("/run", "dbus", "system_bus_socket");
        // final Path path = Path.of("/dev", "random");

        final ReadableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ);

        // LOGGER.info(Files.readString(path));

        // final UnixDomainSocketAddress address = UnixDomainSocketAddress.of(path);
        // // final UnixDomainSocketAddress address = UnixDomainSocketAddress.of("/dev/input/event5");
        //
        // final SocketChannel channel = SocketChannel.open(StandardProtocolFamily.UNIX);
        // channel.connect(address);

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                final ByteBuffer buffer = ByteBuffer.allocate(1024);
                final int bytesRead = channel.read(buffer);

                LOGGER.info("bytesRead = {}", bytesRead);

                if (bytesRead < 0) {
                    return;
                }

                final byte[] bytes = new byte[bytesRead];
                buffer.flip();
                buffer.get(bytes);
                final String message = new String(bytes);

                LOGGER.info(message);
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }, 1L, 1L, TimeUnit.SECONDS);
    }

    private SystemMain() {
        super();
    }
}
