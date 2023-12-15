// Created: 30.11.2020
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
 */
public class CpuTimes {
    private final long guest;
    private final long guestNice;
    private final long idle;
    private final long ioWait;
    private final long irq;
    private final long nice;
    private final long softIrq;
    private final long steal;
    private final long system;
    private final long user;

    public CpuTimes() {
        this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public CpuTimes(final long user, final long nice, final long system, final long idle, final long ioWait, final long irq, final long softIrq, final long steal, final long guest, final long guestNice) {
        super();

        this.user = user;
        this.nice = nice;
        this.system = system;
        this.idle = idle;
        this.ioWait = ioWait;
        this.irq = irq;
        this.softIrq = softIrq;
        this.steal = steal;
        this.guest = guest;
        this.guestNice = guestNice;
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

    public long getGuest() {
        return this.guest;
    }

    public long getGuestNice() {
        return this.guestNice;
    }

    public long getIdle() {
        return this.idle;
    }

    public long getIoWait() {
        return this.ioWait;
    }

    public long getIrq() {
        return this.irq;
    }

    public long getNice() {
        return this.nice;
    }

    public long getSoftIrq() {
        return this.softIrq;
    }

    public long getSteal() {
        return this.steal;
    }

    public long getSystem() {
        return this.system;
    }

    public long getTotal() {
        return getTotalIdle() + getTotalNonIdle();
    }

    public long getTotalIdle() {
        return getIdle() + getIoWait();
    }

    public long getTotalNonIdle() {
        return getUser() + getNice() + getSystem() + getIrq() + getSoftIrq() + getSteal();
    }

    public long getUser() {
        return this.user;
    }
}
