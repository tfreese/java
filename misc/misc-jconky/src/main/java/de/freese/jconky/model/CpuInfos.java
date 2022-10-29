// Created: 05.12.2020
package de.freese.jconky.model;

import java.util.Collections;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class CpuInfos
{
    /**
     *
     */
    private static final CpuInfo DEFAULT_CPU_INFO = new CpuInfo();
    /**
     *
     */
    private final Map<Integer, CpuInfo> infos;

    /**
     * Erstellt ein neues {@link CpuInfos} Object.
     */
    public CpuInfos()
    {
        this(Collections.emptyMap());
    }

    /**
     * Erstellt ein neues {@link CpuInfos} Object.
     *
     * @param infos {@link Map}
     */
    public CpuInfos(final Map<Integer, CpuInfo> infos)
    {
        super();

        this.infos = infos;
    }

    /**
     * Infos pro Kern.
     *
     * @param core int
     *
     * @return {@link CpuInfo}
     */
    public CpuInfo get(final int core)
    {
        return this.infos.getOrDefault(core, DEFAULT_CPU_INFO);
    }

    /**
     * Zusammenfassung des Prozessors.
     *
     * @return {@link CpuInfo}
     */
    public CpuInfo getTotal()
    {
        return get(-1);
    }

    /**
     * @return int
     */
    public int size()
    {
        return this.infos.size();
    }
}
