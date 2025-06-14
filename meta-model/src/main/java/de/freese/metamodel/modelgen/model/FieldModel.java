// Created: 29.07.2018
package de.freese.metamodel.modelgen.model;

import java.util.Objects;

import de.freese.metamodel.ClassUtils;

/**
 * Definiert das Model eines Klassen-Attributs.
 *
 * @author Thomas Freese
 */
public class FieldModel extends AbstractModel {
    private final ClassModel classModel;
    private final String fieldClazzName;

    private Class<?> fieldClazz;
    private boolean isAssoziation;
    private boolean isCollection;
    private boolean useForToStringMethod = true;

    protected FieldModel(final String name, final ClassModel classModel, final String fieldClazzName) {
        super(name);

        this.classModel = Objects.requireNonNull(classModel, "classModel required");
        this.fieldClazzName = Objects.requireNonNull(fieldClazzName, "fieldClazzName required");
    }

    public void addImport(final Class<?> clazz) {
        getClassModel().addImport(clazz);
    }

    public ClassModel getClassModel() {
        return classModel;
    }

    public String getFieldClazzName() {
        return fieldClazzName;
    }

    public String getFieldClazzSimpleName() {
        return ClassUtils.getShortName(getFieldClazzName());
    }

    public boolean isAssoziation() {
        return isAssoziation;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public boolean isFieldClassArray() {
        // return getFieldClazz().isArray();
        return getFieldClazzName().contains("[]");
    }

    public boolean isFieldClassInstanceOf(final Class<?> clazz) {
        final String clazzSimpleName = clazz.getSimpleName();
        final String fieldClassSimpleName = getFieldClazzSimpleName();

        return clazzSimpleName.startsWith(fieldClassSimpleName);
    }

    public boolean isFieldClassPrimitive() {
        // return getFieldClazz().isPrimitive();

        return getFieldClazzName().startsWith("int")
                || getFieldClazzName().startsWith("long")
                || getFieldClazzName().startsWith("float")
                || getFieldClazzName().startsWith("double")
                || getFieldClazzName().startsWith("byte")
                || getFieldClazzName().startsWith("short");
    }

    public boolean isUseForToStringMethod() {
        return useForToStringMethod;
    }

    public void setAssoziation(final boolean isAssoziation) {
        this.isAssoziation = isAssoziation;
    }

    public void setCollection(final boolean isCollection) {
        this.isCollection = isCollection;
    }

    public void setUseForToString(final boolean useForToStringMethod) {
        this.useForToStringMethod = useForToStringMethod;
    }

    synchronized Class<?> getFieldClazz() {
        if (fieldClazz == null) {
            try {

                fieldClazz = ClassUtils.forName(getFieldClazzName(), null);
            }
            catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        return fieldClazz;
    }
}
