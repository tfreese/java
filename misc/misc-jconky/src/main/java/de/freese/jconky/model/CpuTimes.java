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
public class CpuTimes
{
    /**
     *
     */
    private final long guest;
    /**
     *
     */
    private final long guestNice;
    /**
     *
     */
    private final long idle;
    /**
     *
     */
    private final long ioWait;
    /**
     *
     */
    private final long irq;
    /**
     *
     */
    private final long nice;
    /**
     *
     */
    private final long softIrq;
    /**
     *
     */
    private final long steal;
    /**
     *
     */
    private final long system;
    /**
     *
     */
    private final long user;

    /**
     * Erstellt ein neues {@link CpuTimes} Object.
     */
    public CpuTimes()
    {
        this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    /**
     * Erstellt ein neues {@link CpuTimes} Object.
     *
     * @param user long
     * @param nice long
     * @param system long
     * @param idle long
     * @param ioWait long
     * @param irq long
     * @param softIrq long
     * @param steal long
     * @param guest long
     * @param guestNice long
     */
    public CpuTimes(final long user, final long nice, final long system, final long idle, final long ioWait, final long irq, final long softIrq,
                    final long steal, final long guest, final long guestNice)
    {
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
     *
     * @param previous {@link CpuTimes}
     *
     * @return double
     */
    public double getCpuUsage(final CpuTimes previous)
    {
        double totalDiff = (double) getTotal() - previous.getTotal();
        double idleDiff = (double) getTotalIdle() - previous.getTotalIdle();

        double percent = 1D - (idleDiff / totalDiff);

        if (Double.isNaN(percent))
        {
            return 0D;
        }

        return percent;
    }

    /**
     * @return long
     */
    public long getGuest()
    {
        return this.guest;
    }

    /**
     * @return long
     */
    public long getGuestNice()
    {
        return this.guestNice;
    }

    /**
     * @return long
     */
    public long getIdle()
    {
        return this.idle;
    }

    /**
     * @return long
     */
    public long getIoWait()
    {
        return this.ioWait;
    }

    /**
     * @return long
     */
    public long getIrq()
    {
        return this.irq;
    }

    /**
     * @return long
     */
    public long getNice()
    {
        return this.nice;
    }

    /**
     * @return long
     */
    public long getSoftIrq()
    {
        return this.softIrq;
    }

    /**
     * @return long
     */
    public long getSteal()
    {
        return this.steal;
    }

    /**
     * @return long
     */
    public long getSystem()
    {
        return this.system;
    }

    /**
     * @return long
     */
    public long getTotal()
    {
        return getTotalIdle() + getTotalNonIdle();
    }

    /**
     * @return long
     */
    public long getTotalIdle()
    {
        return getIdle() + getIoWait();
    }

    /**
     * @return long
     */
    public long getTotalNonIdle()
    {
        return getUser() + getNice() + getSystem() + getIrq() + getSoftIrq() + getSteal();
    }

    /**
     * @return long
     */
    public long getUser()
    {
        return this.user;
    }
}
