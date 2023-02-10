// Created: 22.04.2020
package de.freese.metamodel.modelgen;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.mapping.ClassType;
import de.freese.metamodel.modelgen.mapping.TypeMapping;
import de.freese.metamodel.modelgen.model.ClassModel;
import de.freese.metamodel.modelgen.model.FieldModel;
import de.freese.metamodel.modelgen.naming.DefaultNamingStrategy;
import de.freese.metamodel.modelgen.naming.NamingStrategy;

/**
 * Erzeugt aus den MetaDaten das CodeModel.
 *
 * @author Thomas Freese
 */
public abstract class AbstractModelGenerator {
    private boolean addFullConstructor;

    private NamingStrategy namingStrategy = new DefaultNamingStrategy();

    private String packageName;

    private boolean serializeable;

    private TypeMapping typeMapping;

    private boolean validationAnnotations;

    public List<ClassModel> generate(final Schema schema) {
        Objects.requireNonNull(schema, "schema required");

        List<ClassModel> classModels = new ArrayList<>(schema.getTables().size());

        for (Table table : schema.getTables()) {
            ClassModel classModel = transformClass(table);
            classModel.setPackageName(getPackageName());

            List<Column> toStringColumns = getColumnsForToString(table);

            // @formatter:off
            classModel.getFields().stream()
                    .filter(field -> {
                        field.setUseForToString(false);

                        Column column = field.getPayload();
                        return toStringColumns.contains(column);
                    })
                    .forEach(field -> field.setUseForToString(true))
                    ;
            // @formatter:on

            classModels.add(classModel);
        }

        return classModels;
    }

    public void setAddFullConstructor(final boolean addFullConstructor) {
        this.addFullConstructor = addFullConstructor;
    }

    public void setNamingStrategy(final NamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public void setSerializeable(final boolean serializeable) {
        this.serializeable = serializeable;
    }

    public void setTypeMapping(final TypeMapping typeMapping) {
        this.typeMapping = typeMapping;
    }

    /**
     * true = jakarta.validation.constraints.* Annotations mit einbauen.
     */
    public void setValidationAnnotations(final boolean validationAnnotations) {
        this.validationAnnotations = validationAnnotations;
    }

    protected List<Column> getColumnsForToString(final Table table) {
        List<Column> columns = null;

        // Finde alle Columns des PrimaryKeys.
        if (table.getPrimaryKey() != null) {
            columns = table.getPrimaryKey().getColumnsOrdered();
        }

        if ((columns == null) || columns.isEmpty()) {
            // Finde alle Columns mit UniqueConstraints.
            // @formatter:off
            columns = table.getUniqueConstraints().stream()
                    .flatMap(uc -> uc.getColumnsOrdered().stream())
                    .sorted(Comparator.comparing(Column::getTableIndex))
                    .toList();
            // @formatter:on
        }

        if ((columns == null) || columns.isEmpty()) {
            // Finde alle Columns mit ForeignKeys.
            // @formatter:off
            columns = table.getColumnsOrdered().stream()
                    .filter(c -> c.getForeignKey() != null)
                    .map(c -> c.getForeignKey().getColumn())
                    .sorted(Comparator.comparing(Column::getTableIndex))
                    .toList();
            // @formatter:on
        }

        if ((columns == null) || columns.isEmpty()) {
            // Alle Columns.
            columns = table.getColumnsOrdered();
        }

        return columns;
    }

    protected NamingStrategy getNamingStrategy() {
        return this.namingStrategy;
    }

    protected String getPackageName() {
        return this.packageName;
    }

    protected TypeMapping getTypeMapping() {
        return this.typeMapping;
    }

    protected boolean isAddFullConstructor() {
        return this.addFullConstructor;
    }

    protected boolean isSerializeable() {
        return this.serializeable;
    }

    protected boolean isValidationAnnotations() {
        return this.validationAnnotations;
    }

    protected ClassModel transformClass(final Table table) {
        String name = getNamingStrategy().getClassName(table.getName());
        ClassModel classModel = new ClassModel(name);
        classModel.setSerializeable(isSerializeable());
        classModel.setAddFullConstructor(isAddFullConstructor());

        transformClassJavaDoc(table, classModel);
        transformClassAnnotations(table, classModel);

        for (Column column : table.getColumnsOrdered()) {
            transformField(column, classModel);
        }

        return classModel;
    }

    protected void transformClassAnnotations(final Table table, final ClassModel classModel) {
        // Empty
    }

    protected abstract void transformClassJavaDoc(Table table, ClassModel classModel);

    protected void transformField(final Column column, final ClassModel classModel) {
        String fieldName = getNamingStrategy().getFieldName(column.getName());
        ClassType type = (ClassType) getTypeMapping().getType(column.getJdbcType(), column.isNullable());

        FieldModel fieldModel = classModel.addField(fieldName, type.getJavaClass());
        fieldModel.setPayload(column);

        transformFieldComments(column, fieldModel);
        transformFieldAnnotations(column, fieldModel);
    }

    protected void transformFieldAnnotations(final Column column, final FieldModel fieldModel) {
        // Validation Annotations
        if (isValidationAnnotations()) {
            if (!column.isNullable()) {
                // @NotNull
                fieldModel.addImport(NotNull.class);
                fieldModel.addAnnotation("@" + NotNull.class.getSimpleName());
            }

            if (JDBCType.VARCHAR.equals(column.getJdbcType())) {
                // @Size(max=50)
                fieldModel.addImport(Size.class);
                fieldModel.addAnnotation("@" + Size.class.getSimpleName() + "(max = " + column.getSize() + ")");
            }
        }
    }

    protected void transformFieldComments(final Column column, final FieldModel fieldModel) {
        String comment = column.getComment();

        if ((comment != null) && !comment.isBlank()) {
            fieldModel.addComment(comment);
        }
    }
}
