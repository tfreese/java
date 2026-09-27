package de.freese.jconky.sensor.host;

import java.lang.management.ManagementFactory;

import de.freese.jconky.sensor.AbstractSensor;

/**
 * @author Thomas Freese
 * @since 27.09.26
 */
public final class HostSensor extends AbstractSensor {
    private static final com.sun.management.OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN =
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private Host host;

    public Host getHost() {
        return host;
    }

    @Override
    public void update() {
        host = new Host(
                OPERATING_SYSTEM_MX_BEAN.getName(),
                OPERATING_SYSTEM_MX_BEAN.getVersion(),
                OPERATING_SYSTEM_MX_BEAN.getArch()
        );
    }
}
