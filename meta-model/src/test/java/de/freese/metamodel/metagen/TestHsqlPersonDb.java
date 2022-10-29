// Created: 08.07.2018
package de.freese.metamodel.metagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Comparator;
import java.util.List;

import javax.sql.DataSource;

import de.freese.metamodel.TestUtil;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.ForeignKey;
import de.freese.metamodel.metagen.model.Index;
import de.freese.metamodel.metagen.model.PrimaryKey;
import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.metagen.model.Sequence;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.metagen.model.UniqueConstraint;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestHsqlPersonDb
{
    /**
     * "PUBLIC"
     */
    private static final String SCHEMA_NAME = "PUBLIC";
    /**
     *
     */
    private static DataSource dataSource;

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @AfterAll
    static void afterAll() throws Exception
    {
        TestUtil.closeDataSource(dataSource);
    }

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        dataSource = TestUtil.createHsqlDBDataSource("jdbc:hsqldb:res:hsqldb/person;create=false;readonly=true");
    }

    /**
     *
     */
    private final MetaExporter metaExporter = new HsqldbMetaExporter();

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    @Order(40)
    void testColumns() throws Exception
    {
        List<Schema> schemas = this.metaExporter.export(dataSource, SCHEMA_NAME, "T_PERSON");

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.get(0).getName());

        List<Table> tables = schemas.get(0).getTables();
        assertNotNull(tables);
        assertEquals(1, tables.size());

        assertEquals("T_PERSON", tables.get(0).getName());

        List<Column> columns = tables.get(0).getColumnsOrdered();
        assertNotNull(columns);
        assertEquals(3, columns.size());

        assertEquals("ID", columns.get(0).getName());
        assertEquals("NAME", columns.get(1).getName());
        assertEquals("VORNAME", columns.get(2).getName());
    }

    /**
     * T_ADDRESS.PERSON_ID -> T_PERSON.ID
     *
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    @Order(60)
    void testForeignKey() throws Exception
    {
        List<Schema> schemas = this.metaExporter.export(dataSource, SCHEMA_NAME, "T_ADDRESS");

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.get(0).getName());

        Table table = schemas.get(0).getTable("T_ADDRESS");
        assertNotNull(table);

        Column column = table.getColumn("PERSON_ID");
        assertNotNull(column);

        ForeignKey foreignKey = column.getForeignKey();
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
     *
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    @Order(70)
    void testIndices() throws Exception
    {
        List<Schema> schemas = this.metaExporter.export(dataSource, SCHEMA_NAME, "T_PERSON");

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.get(0).getName());

        Table table = schemas.get(0).getTable("T_PERSON");
        assertNotNull(table);

        // UniqueConstraint
        List<UniqueConstraint> uniqueConstraints = table.getUniqueConstraints();
        assertNotNull(uniqueConstraints);
        assertEquals(1, uniqueConstraints.size());

        UniqueConstraint uniqueConstraint = uniqueConstraints.get(0);
        assertEquals("PERSON_UNQ", uniqueConstraint.getName());

        List<Column> columns = uniqueConstraint.getColumnsOrdered();
        assertNotNull(columns);
        assertEquals(2, columns.size());
        assertEquals("NAME", columns.get(0).getName());
        assertEquals("VORNAME", columns.get(1).getName());

        // Index
        List<Index> indices = table.getIndices();
        assertNotNull(indices);
        assertEquals(1, indices.size());

        Index index = indices.get(0);
        assertEquals("PERSON_IDX", index.getName());

        columns = index.getColumnsOrdered();
        assertNotNull(columns);
        assertEquals(1, columns.size());
        assertEquals("NAME", columns.get(0).getName());
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    @Order(50)
    void testPrimaryKey() throws Exception
    {
        List<Schema> schemas = this.metaExporter.export(dataSource, SCHEMA_NAME, "T_PERSON");

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.get(0).getName());

        Table table = schemas.get(0).getTable("T_PERSON");
        assertNotNull(table);

        PrimaryKey primaryKey = table.getPrimaryKey();
        assertNotNull(primaryKey);
        assertEquals("PERSON_PK", primaryKey.getName());

        List<Column> columns = primaryKey.getColumnsOrdered();
        assertEquals(1, columns.size());
        assertEquals("ID", columns.get(0).getName());
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    @Order(10)
    void testSchema() throws Exception
    {
        List<Schema> schemas = this.metaExporter.export(dataSource, null, null);

        assertNotNull(schemas);
        assertEquals(3, schemas.size());
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    @Order(20)
    void testSequences() throws Exception
    {
        List<Schema> schemas = this.metaExporter.export(dataSource, SCHEMA_NAME, null);

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.get(0).getName());

        List<Sequence> sequences = schemas.get(0).getSequences();
        assertNotNull(sequences);
        assertEquals(2, sequences.size());

        sequences.sort(Comparator.comparing(Sequence::getName));
        assertEquals("ADDRESS_SEQ", sequences.get(0).getName());
        assertEquals("PERSON_SEQ", sequences.get(1).getName());
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    @Order(30)
    void testTable() throws Exception
    {
        List<Schema> schemas = this.metaExporter.export(dataSource, SCHEMA_NAME, null);

        assertNotNull(schemas);
        assertEquals(1, schemas.size());
        assertEquals(SCHEMA_NAME, schemas.get(0).getName());

        List<Table> tables = schemas.get(0).getTables();
        assertNotNull(tables);
        assertEquals(2, tables.size());

        tables.sort(Comparator.comparing(Table::getName));
        assertEquals("T_ADDRESS", tables.get(0).getName());
        assertEquals("T_PERSON", tables.get(1).getName());
    }
}
