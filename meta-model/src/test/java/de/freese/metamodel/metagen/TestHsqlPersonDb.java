// Created: 08.07.2018
package de.freese.metamodel.metagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Comparator;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.freese.metamodel.TestUtil;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.ForeignKey;
import de.freese.metamodel.metagen.model.Index;
import de.freese.metamodel.metagen.model.PrimaryKey;
import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.metagen.model.Sequence;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.metagen.model.UniqueConstraint;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestHsqlPersonDb {
    private static final String SCHEMA_NAME = "PUBLIC";
    private static DataSource dataSource;

    @AfterAll
    static void afterAll() throws Exception {
        TestUtil.closeDataSource(dataSource);
    }

    @BeforeAll
    static void beforeAll() {
        dataSource = TestUtil.createHsqlDBDataSource("jdbc:hsqldb:res:hsqldb/person;create=false;readonly=true");
    }

    private final MetaExporter metaExporter = new HsqldbMetaExporter();

    @Test
    @Order(40)
    void testColumns() throws Exception {
        final List<Schema> schemas = metaExporter.export(dataSource, SCHEMA_NAME, "T_PERSON");

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.getFirst().getName());

        final List<Table> tables = schemas.getFirst().getTables();
        assertNotNull(tables);
        assertEquals(1, tables.size());

        assertEquals("T_PERSON", tables.getFirst().getName());

        final List<Column> columns = tables.getFirst().getColumnsOrdered();
        assertNotNull(columns);
        assertEquals(3, columns.size());

        assertEquals("ID", columns.get(0).getName());
        assertEquals("NAME", columns.get(1).getName());
        assertEquals("VORNAME", columns.get(2).getName());
    }

    /**
     * T_ADDRESS.PERSON_ID -> T_PERSON.ID
     */
    @Test
    @Order(60)
    void testForeignKey() throws Exception {
        final List<Schema> schemas = metaExporter.export(dataSource, SCHEMA_NAME, "T_ADDRESS");

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.getFirst().getName());

        final Table table = schemas.getFirst().getTable("T_ADDRESS");
        assertNotNull(table);

        final Column column = table.getColumn("PERSON_ID");
        assertNotNull(column);

        final ForeignKey foreignKey = column.getForeignKey();
        assertNotNull(foreignKey);
        assertNotNull(foreignKey.getColumn());
        assertNotNull(foreignKey.getRefColumn());

        assertEquals("FK_PERSON", foreignKey.getName());
        assertEquals("PERSON_ID", foreignKey.getColumn().getName());
        assertEquals("T_ADDRESS", foreignKey.getColumn().getTable().getName());
        assertEquals("ID", foreignKey.getRefColumn().getName());
        assertEquals("T_PERSON", foreignKey.getRefColumn().getTable().getName());
    }

    /**
     * T_ADDRESS.PERSON_ID -> T_PERSON.ID
     */
    @Test
    @Order(70)
    void testIndices() throws Exception {
        final List<Schema> schemas = metaExporter.export(dataSource, SCHEMA_NAME, "T_PERSON");

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.getFirst().getName());

        final Table table = schemas.getFirst().getTable("T_PERSON");
        assertNotNull(table);

        // UniqueConstraint
        final List<UniqueConstraint> uniqueConstraints = table.getUniqueConstraints();
        assertNotNull(uniqueConstraints);
        assertEquals(1, uniqueConstraints.size());

        final UniqueConstraint uniqueConstraint = uniqueConstraints.getFirst();
        assertEquals("PERSON_UNQ", uniqueConstraint.getName());

        List<Column> columns = uniqueConstraint.getColumnsOrdered();
        assertNotNull(columns);
        assertEquals(2, columns.size());
        assertEquals("NAME", columns.get(0).getName());
        assertEquals("VORNAME", columns.get(1).getName());

        // Index
        final List<Index> indices = table.getIndices();
        assertNotNull(indices);
        assertEquals(1, indices.size());

        final Index index = indices.getFirst();
        assertEquals("PERSON_IDX", index.getName());

        columns = index.getColumnsOrdered();
        assertNotNull(columns);
        assertEquals(1, columns.size());
        assertEquals("NAME", columns.getFirst().getName());
    }

    @Test
    @Order(50)
    void testPrimaryKey() throws Exception {
        final List<Schema> schemas = metaExporter.export(dataSource, SCHEMA_NAME, "T_PERSON");

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.getFirst().getName());

        final Table table = schemas.getFirst().getTable("T_PERSON");
        assertNotNull(table);

        final PrimaryKey primaryKey = table.getPrimaryKey();
        assertNotNull(primaryKey);
        assertEquals("PERSON_PK", primaryKey.getName());

        final List<Column> columns = primaryKey.getColumnsOrdered();
        assertEquals(1, columns.size());
        assertEquals("ID", columns.getFirst().getName());
    }

    @Test
    @Order(10)
    void testSchema() throws Exception {
        final List<Schema> schemas = metaExporter.export(dataSource, null, null);

        assertNotNull(schemas);
        assertEquals(3, schemas.size());
    }

    @Test
    @Order(20)
    void testSequences() throws Exception {
        final List<Schema> schemas = metaExporter.export(dataSource, SCHEMA_NAME, null);

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.getFirst().getName());

        final List<Sequence> sequences = schemas.getFirst().getSequences();
        assertNotNull(sequences);
        assertEquals(2, sequences.size());

        sequences.sort(Comparator.comparing(Sequence::getName));
        assertEquals("ADDRESS_SEQ", sequences.get(0).getName());
        assertEquals("PERSON_SEQ", sequences.get(1).getName());
    }

    @Test
    @Order(30)
    void testTable() throws Exception {
        final List<Schema> schemas = metaExporter.export(dataSource, SCHEMA_NAME, null);

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.getFirst().getName());

        final List<Table> tables = schemas.getFirst().getTables();
        assertNotNull(tables);
        assertEquals(2, tables.size());

        tables.sort(Comparator.comparing(Table::getName));
        assertEquals("T_ADDRESS", tables.get(0).getName());
        assertEquals("T_PERSON", tables.get(1).getName());
    }
}
