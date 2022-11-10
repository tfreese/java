// Created: 07.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class ProcessInfo
{
    private final double cpuUsage;

    private final double memoryUsage;

    private final String name;

    private final String owner;

    private final int pid;

    private final String state;

    public ProcessInfo(final int pid, final String state, final String name, final String owner, final double cpuUsage, final double memoryUsage)
    {
        super();

        this.pid = pid;
        this.state = state;
        this.name = name;
        this.owner = owner;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
    }

    public double getCpuUsage()
    {
        return this.cpuUsage;
    }

    public double getMemoryUsage()
    {
        return this.memoryUsage;
    }

    public String getName()
    {
        return this.name;
    }

    public String getOwner()
    {
        return this.owner;
    }

    public int getPid()
    {
        return this.pid;
    }

    /**
     * R Running<br>
     * S Sleeping<br>
     * I Idle<br>
     * D Waiting in uninterruptible disk sleep<br>
     * Z Zombie<br>
     * <br>
     * See: <a href="https://man7.org/linux/man-pages/man5/proc.5.html">man-pages</a><br>
     */
    public String getState()
    {
        return this.state;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append(" pid=").append(this.pid);
        builder.append(", name=").append(this.name);
        builder.append(", owner=").append(this.owner);
        builder.append(", cpuUsage=").append(this.cpuUsage);
        builder.append(", memoryUsage=").append(this.memoryUsage);
        builder.append("]");

        return builder.toString();
    }
}
