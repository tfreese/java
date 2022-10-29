// Created: 28.10.2020
package de.freese.jsensors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.freese.jsensors.backend.async.ExecutorBackend;
import de.freese.jsensors.backend.async.WorkerBackend;
import de.freese.jsensors.backend.disruptor.DisruptorBackend;
import de.freese.jsensors.backend.file.CsvBackend;
import de.freese.jsensors.backend.file.RrdToolBackend;
import de.freese.jsensors.backend.jdbc.JdbcBackend;
import de.freese.jsensors.backend.rsocket.JSensorRSocketServer;
import de.freese.jsensors.backend.rsocket.RSocketBackend;
import de.freese.jsensors.sensor.DefaultSensorValue;
import de.freese.jsensors.sensor.SensorValue;
import org.hsqldb.jdbc.JDBCPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestBackends
{
    /**
     *
     */
    private static final Path LOG_PATH = Paths.get(System.getProperty("user.home"), ".java-apps", "jSensors");
    /**
     *
     */
    private static final long PAUSE_MILLIES = 400L;
    /**
     *
     */
    private static JDBCPool dataSource;

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @AfterAll
    static void afterAll() throws SQLException
    {
        dataSource.close(1);
    }

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        dataSource = new JDBCPool(3);
        // dataSource.setUrl("jdbc:hsqldb:file:logs/hsqldb/sensorDb;create=true;shutdown=true");
        dataSource.setUrl("jdbc:hsqldb:mem:sensorDb;create=true;shutdown=true");
        dataSource.setUser("sa");
        dataSource.setPassword("");
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void testCsvBackend() throws Exception
    {
        Path path = LOG_PATH.resolve("csvBackend.csv");
        Files.deleteIfExists(path);

        CsvBackend backend = new CsvBackend(path, false, 5);
        backend.start();

        createSensorValues().forEach(backend::store);

        backend.stop();

        assertTrue(Files.exists(path));

        List<String> lines = Files.readAllLines(path);
        assertEquals(3, lines.size());
        assertEquals(3, lines.get(0).chars().filter(c -> ((char) c) == ',').count());
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void testCsvBackendExclusive() throws Exception
    {
        for (SensorValue sensorValue : createSensorValues())
        {
            Path path = LOG_PATH.resolve(sensorValue.getName() + ".csv");
            Files.deleteIfExists(path);

            CsvBackend backend = new CsvBackend(path, true, 5);

            backend.start();
            backend.store(sensorValue);
            backend.stop();

            assertTrue(Files.exists(path));

            List<String> lines = Files.readAllLines(path);
            assertEquals(2, lines.size());
            assertEquals(2, lines.get(0).chars().filter(c -> ((char) c) == ',').count());
        }
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void testDisruptorBackend() throws Exception
    {
        List<SensorValue> sensorValues = createSensorValues();
        List<SensorValue> consumedValues = new ArrayList<>();

        DisruptorBackend backend = new DisruptorBackend(consumedValues::add, 3);
        backend.start();

        sensorValues.forEach(backend::store);

        TimeUnit.MILLISECONDS.sleep(PAUSE_MILLIES);

        backend.stop();

        TimeUnit.MILLISECONDS.sleep(PAUSE_MILLIES);

        testValues(sensorValues, consumedValues);
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void testExecutorBackend() throws Exception
    {
        List<SensorValue> sensorValues = createSensorValues();
        List<SensorValue> consumedValues = new ArrayList<>();

        ExecutorBackend backend = new ExecutorBackend(consumedValues::add, Executors.newFixedThreadPool(3));

        sensorValues.forEach(backend::store);

        TimeUnit.MILLISECONDS.sleep(PAUSE_MILLIES);

        testValues(sensorValues, consumedValues);
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void testJdbcBackend() throws Exception
    {
        List<SensorValue> sensorValues = createSensorValues();

        JdbcBackend backend = new JdbcBackend(dataSource, "SENSORS", false, 5);

        backend.start();

        sensorValues.forEach(backend::store);

        backend.stop(); // Trigger submit/commit

        List<SensorValue> dbValues = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select * from sensors order by name asc"))
        {
            while (rs.next())
            {
                dbValues.add(new DefaultSensorValue(rs.getString("NAME"), rs.getString("VALUE"), rs.getLong("TIMESTAMP")));
            }
        }

        assertEquals(2, dbValues.size());

        for (int i = 0; i < dbValues.size(); i++)
        {
            assertEquals(sensorValues.get(i).getName(), dbValues.get(i).getName());
            assertEquals(sensorValues.get(i).getValue(), dbValues.get(i).getValue());
            assertEquals(sensorValues.get(i).getTimestamp(), dbValues.get(i).getTimestamp());
        }
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void testJdbcBackendExclusive() throws Exception
    {
        for (SensorValue sensorValue : createSensorValues())
        {
            JdbcBackend backend = new JdbcBackend(dataSource, "SENSOR_" + sensorValue.getName(), false, 5);

            backend.start();
            backend.store(sensorValue);
            backend.stop(); // Trigger submit/commit

            try (Connection con = dataSource.getConnection();
                 Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery("select * from SENSOR_" + sensorValue.getName()))
            {
                rs.next();

                SensorValue storedValue = new DefaultSensorValue(rs.getString("NAME"), rs.getString("VALUE"), rs.getLong("TIMESTAMP"));

                assertEquals(sensorValue.getName(), storedValue.getName());
                assertEquals(sensorValue.getValue(), storedValue.getValue());
                assertEquals(sensorValue.getTimestamp(), storedValue.getTimestamp());
            }
        }
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    @EnabledOnOs(OS.LINUX)
    void testRRDToolBackend() throws Exception
    {
        for (SensorValue sensorValue : createSensorValues())
        {
            Path path = Path.of("logs", sensorValue.getName() + ".rrd");
            Files.deleteIfExists(path);

            RrdToolBackend backend = new RrdToolBackend(path, 5);

            backend.start();
            backend.store(sensorValue);
            backend.stop();

            assertTrue(Files.exists(path));
        }
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void testRSocketBackend() throws Exception
    {
        List<SensorValue> sensorValues = createSensorValues();
        List<SensorValue> consumedValues = new ArrayList<>();

        // RSocket-Server starten.
        JSensorRSocketServer rSocketServer = new JSensorRSocketServer(consumedValues::add, 7000, 2);
        rSocketServer.start();

        RSocketBackend backend = new RSocketBackend(URI.create("rsocket://localhost:" + 7000), 2);
        backend.start();

        sensorValues.forEach(backend::store);

        TimeUnit.MILLISECONDS.sleep(PAUSE_MILLIES);

        backend.stop();
        rSocketServer.stop();

        TimeUnit.MILLISECONDS.sleep(PAUSE_MILLIES);

        testValues(sensorValues, consumedValues);
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void testWorkerBackend() throws Exception
    {
        List<SensorValue> sensorValues = createSensorValues();
        List<SensorValue> consumedValues = new ArrayList<>();

        WorkerBackend backend = new WorkerBackend(consumedValues::add);
        backend.start();

        sensorValues.forEach(backend::store);

        TimeUnit.MILLISECONDS.sleep(PAUSE_MILLIES);

        backend.stop();

        //        TimeUnit.MILLISECONDS.sleep(PAUSE_MILLIES);

        testValues(sensorValues, consumedValues);
    }

    /**
     * @return {@link List}
     */
    private List<SensorValue> createSensorValues()
    {
        long timestamp = System.currentTimeMillis();

        return List.of(new DefaultSensorValue("test1", "1", timestamp), new DefaultSensorValue("test2", "2", timestamp + 1));
    }

    private void testValues(List<SensorValue> sensorValues, List<SensorValue> consumedValues)
    {
        consumedValues = consumedValues.stream().sorted(Comparator.comparing(SensorValue::getTimestamp)).toList();

        assertEquals(2, consumedValues.size());

        for (int i = 0; i < consumedValues.size(); i++)
        {
            assertEquals(sensorValues.get(i).getName(), consumedValues.get(i).getName());
            assertEquals(sensorValues.get(i).getValue(), consumedValues.get(i).getValue());
            assertEquals(sensorValues.get(i).getTimestamp(), consumedValues.get(i).getTimestamp());
        }
    }
}
