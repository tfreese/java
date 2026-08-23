package de.freese.metamodel.modelgen;

import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.model.ClassModel;

/**
 * @author Thomas Freese
 * @since 22.04.2020
 */
public class PojoModelGenerator extends AbstractModelGenerator {
    @Override
    protected void transformClassJavaDoc(final Table table, final ClassModel classModel) {
        final String comment = table.getComment();

        if (comment != null && !comment.isBlank()) {
            classModel.addComment(comment);
        }

        classModel.addComment("Pojo für Tabelle " + table.getFullName() + ".");
    }
}
