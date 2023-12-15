// Created: 21.04.2020
package de.freese.metamodel.codegen;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Externalizable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.freese.metamodel.codewriter.CodeWriter;
import de.freese.metamodel.codewriter.JavaCodeWriter;
import de.freese.metamodel.modelgen.model.ClassModel;
import de.freese.metamodel.modelgen.model.FieldModel;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestCodeGen {
    @Test
    void testJavaCodeGen() throws Exception {
        final ClassModel classModel = new ClassModel("MyTest");
        classModel.setAddFullConstructor(false);
        classModel.setSerializeable(false);
        classModel.setPackageName("de.freese.test");
        classModel.addComment("Test-JavaCodeGenerator");
        classModel.addComment("@author Thomas Freese");

        classModel.addAnnotation("@Entity");
        classModel.addImport(Entity.class);

        classModel.addInterface(Externalizable.class);

        // Attribute
        FieldModel fieldModel = classModel.addField("myInt", int.class);
        fieldModel.addComment("int-Field");

        fieldModel.addAnnotation("@Column");
        classModel.addImport(Column.class);

        fieldModel = classModel.addField("myBoolean", Boolean.class);
        fieldModel.addComment("Boolean-Field");

        fieldModel = classModel.addField("myList", List.class);
        fieldModel.addComment("List-Field");

        final CodeWriter codeWriter = new JavaCodeWriter();
        codeWriter.write(classModel, System.out);

        assertTrue(true);
    }
}
