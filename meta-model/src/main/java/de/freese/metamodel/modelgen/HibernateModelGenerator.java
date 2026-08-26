package de.freese.metamodel.modelgen;

import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.QueryHint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.model.ClassModel;
import de.freese.metamodel.modelgen.model.FieldModel;

/**
 * @author Thomas Freese
 * @since 22.04.2020
 */
public class HibernateModelGenerator extends JpaModelGenerator {
    @Override
    protected void transformClassAnnotations(final Table table, final ClassModel classModel) {
        super.transformClassAnnotations(table, classModel);

        final String className = classModel.getName();

        classModel.addImport(DynamicInsert.class);
        classModel.addAnnotation("@DynamicInsert");

        classModel.addImport(DynamicUpdate.class);
        classModel.addAnnotation("@DynamicUpdate");

        classModel.addImport(Cache.class);
        classModel.addImport(CacheConcurrencyStrategy.class);
        classModel.addAnnotation("@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = \"" + className.toLowerCase() + "\")");

        classModel.addImport(NamedQueries.class);
        classModel.addImport(NamedQuery.class);
        classModel.addImport(QueryHint.class);

        final String alias = className.substring(0, 1).toLowerCase();

        final String namedQueries = """
                @NamedQueries({
                    @NamedQuery(
                        name = "all_%s", query = "select %s from %s %s"
                        , hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}
                    )
                )}
                """.formatted(className, alias, className, className);

        classModel.addAnnotation(namedQueries);
    }

    @Override
    protected void transformClassJavaDoc(final Table table, final ClassModel classModel) {
        final String comment = table.getComment();

        if (comment != null && !comment.isBlank()) {
            classModel.addComment(comment);
        }

        classModel.addComment("Hibernate-Entity für Tabelle " + table.getFullName() + ".");
    }

    @Override
    protected void transformFieldAnnotations(final Column column, final FieldModel fieldModel) {
        super.transformFieldAnnotations(column, fieldModel);

        if (fieldModel.isAssoziation()) {
            // Assoziation = Collections und Objekt-Referenzen.
            fieldModel.getClassModel().addImport(CacheConcurrencyStrategy.class);
            fieldModel.getClassModel().addImport(Cache.class);

            if (fieldModel.isCollection()) {
                fieldModel.getClassModel().addImport(Fetch.class);
                fieldModel.getClassModel().addImport(FetchMode.class);

                fieldModel.addAnnotation("@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = \"collections\")");
                fieldModel.addAnnotation("@Fetch(FetchMode.SELECT)");
            }
            else {
                fieldModel.addAnnotation("@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = \"" + fieldModel.getName() + "\")");
            }
        }
    }
}
