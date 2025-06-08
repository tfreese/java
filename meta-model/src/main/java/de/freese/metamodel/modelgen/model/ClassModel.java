// Created: 29.07.2018
package de.freese.metamodel.modelgen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Definiert das Model einer Klasse.
 *
 * @author Thomas Freese
 */
public class ClassModel extends AbstractModel {
    private final List<FieldModel> fields = new ArrayList<>();
    private final Set<String> imports = new TreeSet<>();
    private final List<Class<?>> interfaces = new ArrayList<>();

    private boolean addFullConstructor;
    private String packageName;

    private boolean serializeable = true;

    public ClassModel(final String name) {
        super(name);
    }

    public FieldModel addField(final String name, final Class<?> fieldClazz) {
        return addField(name, fieldClazz.getName());
    }

    public FieldModel addField(final String name, final String fieldClazzName) {
        final FieldModel fieldModel = new FieldModel(name, this, fieldClazzName);
        fields.add(fieldModel);

        return fieldModel;
    }

    public void addImport(final Class<?> clazz) {
        addImport(clazz.getName());
    }

    public void addImport(final String clazzName) {
        imports.add(clazzName);
    }

    public void addInterface(final Class<?> iface) {
        interfaces.add(iface);

        addImport(iface);
    }

    public List<FieldModel> getFields() {
        return fields;
    }

    public Set<String> getImports() {
        final Set<String> set = new TreeSet<>(imports);

        getFields().stream()
                .filter(field -> !field.isFieldClassPrimitive())
                .filter(field -> !field.isFieldClassArray())
                .forEach(field -> set.add(field.getFieldClazzName()))
        ;

        return set;
    }

    public List<Class<?>> getInterfaces() {
        return interfaces;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isAddFullConstructor() {
        return addFullConstructor;
    }

    public boolean isSerializeable() {
        return serializeable;
    }

    public void setAddFullConstructor(final boolean addFullConstructor) {
        this.addFullConstructor = addFullConstructor;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public void setSerializeable(final boolean serializeable) {
        this.serializeable = serializeable;
    }
}
