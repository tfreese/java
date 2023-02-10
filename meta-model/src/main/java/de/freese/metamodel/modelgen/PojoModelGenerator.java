// Created: 22.04.2020
package de.freese.metamodel.modelgen;

import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.model.ClassModel;

/**
 * @author Thomas Freese
 */
public class PojoModelGenerator extends AbstractModelGenerator {
    /**
     * @see de.freese.metamodel.modelgen.AbstractModelGenerator#transformClassJavaDoc(de.freese.metamodel.metagen.model.Table,
     * de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void transformClassJavaDoc(final Table table, final ClassModel classModel) {
        String comment = table.getComment();

        if ((comment != null) && !comment.isBlank()) {
            classModel.addComment(comment);
        }

        classModel.addComment("Pojo für Tabelle " + table.getFullName() + ".");
    }
}
