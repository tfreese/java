// Created: 05.12.2020
package de.freese.jconky.model;

import java.util.Collections;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class CpuInfos
{
    private static final CpuInfo DEFAULT_CPU_INFO = new CpuInfo();

    private final Map<Integer, CpuInfo> infos;

    public CpuInfos()
    {
        this(Collections.emptyMap());
    }

    public CpuInfos(final Map<Integer, CpuInfo> infos)
    {
        super();

        this.infos = infos;
    }

    public CpuInfo get(final int core)
    {
        return this.infos.getOrDefault(core, DEFAULT_CPU_INFO);
    }

    public CpuInfo getTotal()
    {
        return get(-1);
    }

    public int size()
    {
        return this.infos.size();
    }
}
