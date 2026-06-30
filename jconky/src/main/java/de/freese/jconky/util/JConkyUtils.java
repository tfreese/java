// Created: 10.12.2020
package de.freese.jconky.util;

/**
 * @author Thomas Freese
 */
public final class JConkyUtils {
    /**
     * Periodendauer des Timer-Interrupts.<br>
     * Betriebssystem spezifischer Faktor, bei Linux in der Regel 100.<br>
     * ArchLinux: getconf CLK_TCK;<br>
     */
    public static final int USER_HZ = 100;
    private static final String[] SIZE_UNITS = new String[]{"B", "K", "M", "G", "T", "P", "E"};

    public static double jiffieToMillies(final double jiffie) {
        return jiffieToMillies(jiffie, USER_HZ);
    }

    /**
     * @param userHz int Betriebssystem spezifischer Faktor, bei Linux in der Regel 100.<br>
     * ArchLinux: getconf CLK_TCK;<br>
     */
    public static double jiffieToMillies(final double jiffie, final int userHz) {
        final double multiplier = 1000D / userHz;

        return jiffie * multiplier;
    }

    public static double jiffieToSeconds(final double jiffie) {
        return jiffieToSeconds(jiffie, USER_HZ);
    }

    /**
     * @param userHz int Betriebssystem spezifischer Faktor, bei Linux in der Regel 100.<br>
     * ArchLinux: getconf CLK_TCK;<br>
     */
    public static double jiffieToSeconds(final double jiffie, final int userHz) {
        return jiffie / userHz;
    }

    /**
     * @return String, z.B. 'HH:mm:ss', oder 'm:ss'
     */
    public static String toClockString(final double duration) {
        return toClockString(duration, "%02d:%02d:%02d", "%02d:%02d");
    }

    /**
     * @param withHourPattern String; %02d:%02d:%02d
     * @param withoutHourPattern String; %1d:%02d
     */
    public static String toClockString(final double duration, final String withHourPattern, final String withoutHourPattern) {
        final int seconds = (int) duration % 60;
        final int minutes = (int) (duration / 60) % 60;
        final int hours = (int) (duration / 60 / 60) % 60;

        String clock = null;

        if (hours > 0) {
            clock = String.format(withHourPattern, hours, minutes, seconds);
        }
        else {
            clock = String.format(withoutHourPattern, minutes, seconds);
        }

        return clock;
    }

    /**
     * @return String, z.B. '___,___ MB'
     */
    public static String toHumanReadableSize(final double size) {
        return toHumanReadableSize(size, "%.1f %s");
    }

    /**
     * @return String, z.B. '___,___ MB'
     */
    public static String toHumanReadableSize(final double size, final String format) {
        int unitIndex = 0;

        if (size > 0) {
            unitIndex = (int) (Math.log10(size) / 3D);
        }

        final double unitValue = 1 << (unitIndex * 10);

        // // String readableSize = new DecimalFormat("#,##0.#").format(size / unitValue) + " " + SIZE_UNITS[unitIndex];
        // // String readableSize = String.format("%7.0f %s", size / unitValue, SIZE_UNITS[unitIndex]);
        return String.format(format, size / unitValue, SIZE_UNITS[unitIndex]);

        // Variante 2: Micrometer
        // io.micrometer.core.instrument.logging.LoggingMeterRegistry.Printer.humanReadableByteCount(double)
        // int unit = 1024;
        // if (bytes < unit || Double.isNaN(bytes)) return decimalOrNan(bytes) + " B";
        // int exp = (int) (Math.log(bytes) / Math.log(unit));
        // String pre = "KMGTPE".charAt(exp - 1) + "i";
        // return decimalOrNan(bytes / Math.pow(unit, exp)) + " " + pre + "B";

        // Variante 3:
        // double divider = 1D;
        // String unit = "";
        //
        // if (size < 1024)
        // {
        // divider = 1D;
        // unit = "B";
        // }
        // else if (size < 1_048_576)
        // {
        // divider = 1024D;
        // unit = "KB";
        // }
        // else if (size < 1_073_741_824)
        // {
        // divider = 1_048_576D;
        // unit = "MB";
        // }
        // else if (size < (1_048_576 * 1_048_576))
        // {
        // divider = 1_073_741_824D;
        // unit = "GB";
        // }
        //
        // String readableSize = String.format("%.1f %s", size / divider, unit);
        //
        // return readableSize;

        // Variante 4:
        // double value = Math.abs(size);
        //
        // if (value < 1024D)
        // {
        // return size + " B";
        // }
        //
        // CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        //
        // while (value > 1024D)
        // {
        // value /= 1024;
        // ci.next();
        // }
        //
        // return String.format("%.1f %cB", value, ci.previous());
    }

    private JConkyUtils() {
        super();
    }
}
