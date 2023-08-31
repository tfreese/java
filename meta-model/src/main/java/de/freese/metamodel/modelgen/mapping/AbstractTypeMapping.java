// Created: 22.07.2018
package de.freese.metamodel.modelgen.mapping;

import java.sql.JDBCType;
import java.util.Map;
import java.util.TreeMap;

/**
 * Basis-Implementierung des Typ-Mappings.
 *
 * @author Thomas Freese
 */
public abstract class AbstractTypeMapping implements TypeMapping {
    private final Map<JDBCType, Type> notNullMappings = new TreeMap<>();

    private final Map<JDBCType, Type> nullableMappings = new TreeMap<>();

    @Override
    public Type getType(final JDBCType jdbcType, final boolean nullable) {
        Type type = null;

        if (nullable) {
            type = this.nullableMappings.get(jdbcType);
        }
        else {
            type = this.notNullMappings.get(jdbcType);
        }

        if (type == null) {
            throw new IllegalStateException("no mapping found for JDBCType " + jdbcType);
        }

        return type;
    }

    /**
     * Verknüpft einen {@link JDBCType} mit seiner Code-Repräsentation.
     */
    protected void register(final JDBCType jdbcType, final Type nullableType) {
        register(jdbcType, nullableType, nullableType);
    }

    /**
     * Verknüpft einen {@link JDBCType} mit seiner Code-Repräsentation.
     */
    protected void register(final JDBCType jdbcType, final Type nullableType, final Type notNullType) {
        this.nullableMappings.put(jdbcType, nullableType);
        this.notNullMappings.put(jdbcType, notNullType);
    }

    // default sql types
    // setTypeMapping(Types.CHAR, "CHAR");
    // setTypeMapping(Types.BIGINT, "BIGINT");
    // setTypeMapping(Types.DATE, "DATE");
    // setTypeMapping(Types.DECIMAL, "DECIMAL");
    // setTypeMapping(Types.DOUBLE, "DOUBLE");
    // setTypeMapping(Types.INTEGER, "INTEGER");
    // setTypeMapping(Types.LONGVARCHAR, "LONGVARCHAR");
    // setTypeMapping(Types.TIMESTAMP, "TIMESTAMP");
    // setTypeMapping(Types.TINYINT, "TINYINT");
    // setTypeMapping(Types.REAL, "REAL");
    // setTypeMapping(Types.VARCHAR, "VARCHAR");

    // addTypeNameToCode("null", Types.NULL);
    // addTypeNameToCode("datalink", Types.DATALINK);
    // addTypeNameToCode("numeric", Types.NUMERIC);
    // addTypeNameToCode("smallint", Types.SMALLINT);
    // addTypeNameToCode("float", Types.FLOAT);
    // addTypeNameToCode("longnvarchar", Types.LONGNVARCHAR);
    // addTypeNameToCode("nchar", Types.NCHAR);
    // addTypeNameToCode("boolean", Types.BOOLEAN);
    // addTypeNameToCode("nvarchar", Types.NVARCHAR);
    // addTypeNameToCode("rowid", Types.ROWID);
    // addTypeNameToCode("bit", Types.BIT);
    // addTypeNameToCode("time", Types.TIME);
    // addTypeNameToCode("other", Types.OTHER);
    // addTypeNameToCode("longvarbinary", Types.LONGVARBINARY);
    // addTypeNameToCode("varbinary", Types.VARBINARY);
    // addTypeNameToCode("binary", Types.BINARY);
    // addTypeNameToCode("struct", Types.STRUCT);
    // addTypeNameToCode("array", Types.ARRAY);
    // addTypeNameToCode("java_object", Types.JAVA_OBJECT);
    // addTypeNameToCode("distinct", Types.DISTINCT);
    // addTypeNameToCode("ref", Types.REF);
    // addTypeNameToCode("blob", Types.BLOB);
    // addTypeNameToCode("clob", Types.CLOB);
    // addTypeNameToCode("nclob", Types.NCLOB);
    // addTypeNameToCode("sqlxml", Types.SQLXML);
}
