// Created: 07.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public record ProcessInfo(int pid, String state, String name, String owner, double cpuUsage, double memoryUsage) {

    /**
     * R Running<br>
     * S Sleeping<br>
     * I Idle<br>
     * D Waiting in uninterruptible disk sleep<br>
     * Z Zombie<br>
     * <br>
     * See: <a href="https://man7.org/linux/man-pages/man5/proc.5.html">man-pages</a><br>
     */
    @Override
    public String state() {
        return state;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "["
                + " pid=" + pid
                + ", name=" + name
                + ", state=" + state
                + ", cpuUsage=" + cpuUsage
                + ", owner=" + owner
                + ", cpuUsage=" + cpuUsage
                + ", memoryUsage=" + memoryUsage
                + "]";
    }
}
