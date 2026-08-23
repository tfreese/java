package de.freese.jconky.model;

/**
 * cat /proc/stat<br>
 * cpu 247721 450 70350 2534219 43469 8434 2372 0 0 0<br>
 * cpu0 32043 74 7737 310311 11885 570 673 0 0 0<br>
 * cpu1 26344 48 11872 312233 6875 5683 232 0 0 0<br>
 * [...]<br>
 * user nice system idle iowait irq softirq steal guest guest_nice<br>
 * <br>
 * Diese Zahlen sind sogenannte Jiffies (CPU Clock Ticks), CPU-Zeit-Einheit.<br>
 * Ein Jiffie ist der Anteil eines CPU-Zyklus, der für die Ausführung eines Befehls benötigt wurde.<br>
 * Oder auch benannt als: Periodendauer des Timer-Interrupts<br>
 *
 * @author Thomas Freese
 * @since 30.11.2020
 */
public record CpuTimes(long user, long nice, long system, long idle, long ioWait, long irq, long softIrq, long steal, long guest, long guestNice) {
    public CpuTimes() {
        this(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
    }

    /**
     * Liefert die CPU-Auslastung von 0 bis 1.<br>
     */
    public double getCpuUsage(final CpuTimes previous) {
        final double totalDiff = (double) getTotal() - previous.getTotal();
        final double idleDiff = (double) getTotalIdle() - previous.getTotalIdle();

        final double percent = 1D - (idleDiff / totalDiff);

        if (Double.isNaN(percent)) {
            return 0D;
        }

        return percent;
    }

    public long getTotal() {
        return getTotalIdle() + getTotalNonIdle();
    }

    public long getTotalIdle() {
        return idle() + ioWait();
    }

    public long getTotalNonIdle() {
        return user() + nice() + system() + irq() + softIrq() + steal();
    }
}
