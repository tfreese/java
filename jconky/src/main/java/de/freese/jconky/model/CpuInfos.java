package de.freese.jconky.model;

import java.util.Map;

/**
 * @author Thomas Freese
 * @since 05.12.2020
 */
public class CpuInfos {
    private static final CpuInfo DEFAULT_CPU_INFO = new CpuInfo();

    private final Map<Integer, CpuInfo> infos;

    public CpuInfos() {
        this(Map.of());
    }

    public CpuInfos(final Map<Integer, CpuInfo> infos) {
        super();

        this.infos = infos;
    }

    public CpuInfo get(final int core) {
        return infos.getOrDefault(core, DEFAULT_CPU_INFO);
    }

    public CpuInfo getTotal() {
        return get(-1);
    }

    public int size() {
        return infos.size();
    }
}
