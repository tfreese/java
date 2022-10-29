//Created: 08.07.2018
package de.freese.metamodel.metagen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class MetaResolver
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaResolver.class);

    /**
     * Liefert auf Basis der DB-MetaDaten den entsprechenden Exporter für die Quelle.
     *
     * @param dataSource {@link DataSource}
     *
     * @return {@link MetaExporter}
     *
     * @throws SQLException Falls was schiefgeht.
     * @throws IllegalStateException, wenn keine Quelle ermittelt werden konnte.
     */
    public static MetaExporter determineMetaData(final DataSource dataSource) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData metaData = connection.getMetaData();

            String product = metaData.getDatabaseProductName().toLowerCase();
            product = product.split(" ")[0];
            // int majorVersion = metaData.getDatabaseMajorVersion();
            // int minorVersion = metaData.getDatabaseMinorVersion();

            return switch (product)
                    {
                        case "oracle" -> new OracleMetaExporter();
                        case "hsql" -> new HsqldbMetaExporter();
                        case "mysql" -> new MariaDbMetaExporter();
                        case "sqlite" -> new SQLiteMetaExporter();
                        default ->
                        {
                            String msg = String.format("No MetaModelGenerator found for: %s%n", metaData.getDatabaseProductName());
                            LOGGER.error(msg);
                            throw new IllegalStateException(msg);
                        }
                    };
        }
    }

    /**
     * Erstellt ein neues {@link MetaResolver} Object.
     */
    private MetaResolver()
    {
        super();
    }
}
