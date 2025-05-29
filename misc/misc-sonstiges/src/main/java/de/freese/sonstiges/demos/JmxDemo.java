// Created: 13.07.2016
package de.freese.sonstiges.demos;

import java.lang.management.ManagementFactory;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DatabaseDriver;

/**
 * @author Thomas Freese
 */
public final class JmxDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmxDemo.class);

    private static final Random RANDOM = new SecureRandom();

    /**
     * The model MXBean class MUST implement an interface with the following name: "model class name" plus MXBean.<br>
     * Das Interface muss public sein.
     *
     * @author Thomas Freese
     */
    @FunctionalInterface
    public interface DateMXBean {
        String getCurrentTime();
    }

    public static void main(final String[] args) throws Exception {
        // Siehe auch org.springframework.jmx.support.JmxUtils

        try (ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5)) {
            final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

            // Eigene MBean registrieren.
            mBeanServer.registerMBean((DateMXBean) () -> LocalDateTime.now().toString(), new ObjectName("bean:name=dateBean"));
            // final DateMXBean dateBeanProxy = JMX.newMBeanProxy(mBeanServer, new ObjectName("bean:name=dateBean"), DateMXBean.class);

            // jdbc:h2:~/test;MODE=Oracle;DEFAULT_NULL_ORDERING=HIGH
            final HikariConfig config = new HikariConfig();
            config.setDriverClassName(DatabaseDriver.H2.getDriverClassName());
            config.setJdbcUrl("jdbc:h2:mem:jmx");
            config.setUsername("sa");
            config.setPassword("");
            config.setMaximumPoolSize(3);
            config.setMinimumIdle(1);
            config.setScheduledExecutor(scheduledExecutorService);
            config.setPoolName("HikariConnectionPool");
            config.setRegisterMbeans(true);

            try (HikariDataSource dataSource = new HikariDataSource(config)) {
                // Trigger Hikari initialisation.
                dataSource.getConnection().close();

                final ObjectName poolName = new ObjectName("com.zaxxer.hikari:type=Pool (" + config.getPoolName() + ")");
                final HikariPoolMXBean poolProxy = JMX.newMXBeanProxy(mBeanServer, poolName, HikariPoolMXBean.class);

                scheduledExecutorService.scheduleWithFixedDelay(() -> {
                    try {
                        if (poolProxy == null) {
                            LOGGER.info("Hikari not initialized, please wait...");
                        }
                        else {
                            LOGGER.info("HikariPoolState: Active={}; Idle={}, Total={}, WaitingThreads={}",
                                    poolProxy.getActiveConnections(),
                                    poolProxy.getIdleConnections(),
                                    poolProxy.getTotalConnections(),
                                    poolProxy.getThreadsAwaitingConnection());
                        }
                    }
                    catch (Throwable ex) {
                        LOGGER.error(ex.getMessage());
                    }
                }, 100L, 1000L, TimeUnit.MILLISECONDS);
                scheduledExecutorService.scheduleWithFixedDelay(() -> {
                    try {
                        final ObjectName on = ObjectName.getInstance("com.zaxxer.hikari:type=Pool (HikariConnectionPool)");
                        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

                        LOGGER.info("ActiveConnections = {}", mbs.getAttribute(on, "ActiveConnections"));
                        LOGGER.info("IdleConnections = {}", mbs.getAttribute(on, "IdleConnections"));

                        final DateMXBean dateMXBeanProxy = JMX.newMXBeanProxy(mbs, new ObjectName("bean:name=dateBean"), DateMXBean.class);
                        LOGGER.info("DateBean: {}", dateMXBeanProxy.getCurrentTime());
                        LOGGER.info("DateBean: {}", mBeanServer.getAttribute(ObjectName.getInstance("bean:name=dateBean"), "CurrentTime"));
                        // LOGGER.info("DateBean: {}", mBeanServer.invoke(ObjectName.getInstance("bean:name=dateBean"), "getCurrentTime", null, null));

                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage());
                    }
                }, 1L, 3L, TimeUnit.SECONDS);

                final Callable<Void> job = () -> {
                    final String query = "VALUES NOW()";
                    // final String query = DatabaseDriver.H2.getValidationQuery();

                    for (int i = 0; i < 10; i++) {
                        try (Connection connection = dataSource.getConnection();
                             Statement statement = connection.createStatement();
                             ResultSet resultSet = statement.executeQuery(query)) {
                            resultSet.next();

                            LOGGER.info("Query: {}", resultSet.getObject(1));
                            TimeUnit.MILLISECONDS.sleep(RANDOM.nextLong(100L, 1500L));
                        }

                        TimeUnit.MILLISECONDS.sleep(1000L);
                    }

                    return null;
                };

                final Future<Void> future1 = scheduledExecutorService.submit(job);
                final Future<Void> future2 = scheduledExecutorService.submit(job);

                // Avoid Terminating
                // System.in.read();
                future1.get();
                future2.get();
            }

            System.exit(0);
        }
    }

    private JmxDemo() {
        super();
    }
}
