// Created: 22.07.2018
package de.freese.metamodel.modelgen.mapping;

import java.sql.JDBCType;
import java.util.Date;

/**
 * {@link JDBCType}-Mapping für Java.
 *
 * @author Thomas Freese
 */
public class JavaTypeMapping extends AbstractTypeMapping
{
    /**
     * Erstellt ein neues {@link JavaTypeMapping} Object.
     */
    public JavaTypeMapping()
    {
        super();

        register(JDBCType.BIGINT, new ClassType(Long.class), new ClassType(long.class));
        register(JDBCType.BINARY, new ClassType(byte[].class));
        register(JDBCType.BIT, new ClassType(Boolean.class), new ClassType(boolean.class));
        register(JDBCType.BLOB, new ClassType(byte[].class));
        register(JDBCType.BOOLEAN, new ClassType(Boolean.class), new ClassType(boolean.class));
        register(JDBCType.CHAR, new ClassType(Character.class), new ClassType(char.class));
        register(JDBCType.CLOB, new ClassType(String.class));
        register(JDBCType.DATE, new ClassType(Date.class)); // LocalDateTime
        register(JDBCType.DECIMAL, new ClassType(Long.class), new ClassType(long.class));
        register(JDBCType.DOUBLE, new ClassType(Double.class), new ClassType(double.class));
        register(JDBCType.FLOAT, new ClassType(Float.class), new ClassType(float.class));
        register(JDBCType.INTEGER, new ClassType(Integer.class), new ClassType(int.class));
        register(JDBCType.NUMERIC, new ClassType(Double.class), new ClassType(double.class));
        register(JDBCType.SMALLINT, new ClassType(Short.class), new ClassType(short.class));
        register(JDBCType.TIME, new ClassType(Date.class));
        register(JDBCType.TIMESTAMP, new ClassType(Date.class));
        register(JDBCType.VARCHAR, new ClassType(String.class));
    }
}
