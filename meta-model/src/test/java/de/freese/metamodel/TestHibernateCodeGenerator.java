// Created: 08.07.2018
package de.freese.metamodel;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.freese.metamodel.codewriter.AbstractCodeWriter;
import de.freese.metamodel.codewriter.JavaCodeWriter;
import de.freese.metamodel.metagen.HsqldbMetaExporter;
import de.freese.metamodel.metagen.MetaExporter;
import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.modelgen.AbstractModelGenerator;
import de.freese.metamodel.modelgen.HibernateModelGenerator;
import de.freese.metamodel.modelgen.mapping.JavaTypeMapping;
import de.freese.metamodel.modelgen.model.ClassModel;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestHibernateCodeGenerator {
    private static DataSource dataSource;

    @AfterAll
    static void afterAll() throws Exception {
        TestUtil.closeDataSource(dataSource);
    }

    @BeforeAll
    static void beforeAll() {
        dataSource = TestUtil.createHsqlDBDataSource("jdbc:hsqldb:res:hsqldb/person;create=false;readonly=true");
    }

    @Test
    @Order(1)
    void testCreate() throws Exception {
        // MetaDaten extrahieren-
        MetaExporter metaExporter = new HsqldbMetaExporter();
        List<Schema> schemas = metaExporter.export(dataSource, null, null);

        // MetaDaten in ClassModel umwandeln.
        AbstractModelGenerator modelGenerator = new HibernateModelGenerator();
        modelGenerator.setAddFullConstructor(true);
        // modelGenerator.setNamingStrategy(new DefaultNamingStrategy());
        modelGenerator.setPackageName("test.hibernate");
        modelGenerator.setSerializeable(true);
        modelGenerator.setTypeMapping(new JavaTypeMapping());
        modelGenerator.setValidationAnnotations(true);

        Path path = Paths.get("src/test/generated");
        Path pathHibernate = path.resolve("test").resolve("hibernate");

        Files.createDirectories(pathHibernate);

        Files.deleteIfExists(path.resolve("Address.java"));
        Files.deleteIfExists(path.resolve("Person.java"));

        // ClassModel als Code schreiben.
        AbstractCodeWriter codeWriter = new JavaCodeWriter();

        for (Schema schema : schemas) {
            List<ClassModel> classModels = modelGenerator.generate(schema);

            for (ClassModel classModel : classModels) {
                Path pathFile = pathHibernate.resolve(classModel.getName() + codeWriter.getFileExtension());

                try (PrintStream ps = new PrintStream(new BufferedOutputStream(Files.newOutputStream(pathFile)), true, StandardCharsets.UTF_8)) {
                    codeWriter.write(classModel, ps);

                    ps.flush();
                }
            }
        }

        assertTrue(Files.exists(pathHibernate.resolve("Address.java")));
        assertTrue(Files.exists(pathHibernate.resolve("Person.java")));
    }
}
