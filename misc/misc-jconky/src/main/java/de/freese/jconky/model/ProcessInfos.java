// Created: 07.12.2020
package de.freese.jconky.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Thomas Freese
 */
public class ProcessInfos
{
    /**
    *
    */
    private static final Predicate<ProcessInfo> PREDICATE_IDLE = ph -> "I".equals(ph.getState());
    /**
     *
     */
    private static final Predicate<ProcessInfo> PREDICATE_RUNNING = ph -> "R".equals(ph.getState());
    /**
     *
     */
    private static final Predicate<ProcessInfo> PREDICATE_SLEEPING = ph -> "S".equals(ph.getState());
    /**
     *
     */
    private static final Predicate<ProcessInfo> PREDICATE_WAITING = ph -> "W".equals(ph.getState());
    /**
     *
     */
    private final List<ProcessInfo> infos;

    /**
     * Erstellt ein neues {@link ProcessInfos} Object.
     */
    public ProcessInfos()
    {
        this(Collections.emptyList());
    }

    /**
     * Erstellt ein neues {@link ProcessInfos} Object.
     *
     * @param infos {@link List}
     */
    public ProcessInfos(final List<ProcessInfo> infos)
    {
        super();

        this.infos = infos;
    }

    /**
     * @return int
     */
    public int getAlive()
    {
        Predicate<ProcessInfo> predicateAlive = PREDICATE_RUNNING.or(PREDICATE_SLEEPING).or(PREDICATE_WAITING).or(PREDICATE_IDLE);

        return (int) this.infos.stream().filter(predicateAlive).count();
    }

    /**
     * @return int
     */
    public int getRunning()
    {
        return (int) this.infos.stream().filter(PREDICATE_RUNNING).count();
    }

    /**
     * Liefert die höchsten N Prozesse.
     *
     * @param count int
     *
     * @return {@link List}
     */
    public List<ProcessInfo> getSortedByCpuUsage(final int count)
    {
        return this.infos.stream().sorted(Comparator.comparing(ProcessInfo::getCpuUsage).reversed()).limit(count).toList();
    }

    /**
     * Liefert die höchsten N Prozesse.
     *
     * @param count int
     *
     * @return {@link List}
     */
    public List<ProcessInfo> getSortedByMemoryUsage(final int count)
    {
        return this.infos.stream().sorted(Comparator.comparing(ProcessInfo::getMemoryUsage).reversed()).limit(count).toList();
    }

    /**
     * @param count int
     *
     * @return {@link List}
     */
    public List<ProcessInfo> getSortedByName(final int count)
    {
        return this.infos.stream().sorted(Comparator.comparing(ProcessInfo::getName)).limit(count).toList();
    }

    /**
     * @return int
     */
    public int size()
    {
        return this.infos.size();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("size=").append(size());
        builder.append("]");

        return builder.toString();
    }
}
