// Created: 29.07.2018
package de.freese.metamodel.modelgen.model;

import java.util.Objects;

import de.freese.metamodel.ClassUtils;

/**
 * Definiert das Model eines Klassen-Attributs.
 *
 * @author Thomas Freese
 */
public class FieldModel extends AbstractModel
{
    /**
     *
     */
    private final ClassModel classModel;
    /**
     *
     */
    private final String fieldClazzName;
    /**
     *
     */
    private Class<?> fieldClazz;
    /**
     *
     */
    private boolean isAssoziation;
    /**
     *
     */
    private boolean isCollection;
    /**
     *
     */
    private boolean useForToStringMethod = true;

    /**
     * Erstellt ein neues {@link FieldModel} Object.
     *
     * @param name String
     * @param classModel {@link ClassModel}
     * @param fieldClazzName String
     */
    protected FieldModel(final String name, final ClassModel classModel, final String fieldClazzName)
    {
        super(name);

        this.classModel = Objects.requireNonNull(classModel, "classModel required");
        this.fieldClazzName = Objects.requireNonNull(fieldClazzName, "fieldClazzName required");
    }

    /**
     * @param clazz {@link Class}
     */
    public void addImport(final Class<?> clazz)
    {
        getClassModel().addImport(clazz);
    }

    /**
     * @return {@link ClassModel}
     */
    public ClassModel getClassModel()
    {
        return this.classModel;
    }

    /**
     * @return String
     */
    public String getFieldClazzName()
    {
        return this.fieldClazzName;
    }

    /**
     * @return String
     */
    public String getFieldClazzSimpleName()
    {
        return ClassUtils.getShortName(getFieldClazzName());
    }

    /**
     * @return boolean
     */
    public boolean isAssoziation()
    {
        return this.isAssoziation;
    }

    /**
     * @return boolean
     */
    public boolean isCollection()
    {
        return this.isCollection;
    }

    /**
     * @return boolean
     */
    public boolean isFieldClassArray()
    {
        // return getFieldClazz().isArray();
        return getFieldClazzName().contains("[]");
    }

    /**
     * @param clazz Class
     *
     * @return boolean
     */
    public boolean isFieldClassInstanceOf(final Class<?> clazz)
    {
        String clazzSimpleName = clazz.getSimpleName();
        String fieldClassSimpleName = getFieldClazzSimpleName();

        return clazzSimpleName.startsWith(fieldClassSimpleName);
    }

    /**
     * @return boolean
     */
    public boolean isFieldClassPrimitive()
    {
        // return getFieldClazz().isPrimitive();

        boolean isPrimitive = false;

        if (getFieldClazzName().startsWith("int"))
        {
            isPrimitive = true;
        }
        else if (getFieldClazzName().startsWith("long"))
        {
            isPrimitive = true;
        }
        else if (getFieldClazzName().startsWith("float"))
        {
            isPrimitive = true;
        }
        else if (getFieldClazzName().startsWith("double"))
        {
            isPrimitive = true;
        }
        else if (getFieldClazzName().startsWith("byte"))
        {
            isPrimitive = true;
        }
        else if (getFieldClazzName().startsWith("short"))
        {
            isPrimitive = true;
        }

        return isPrimitive;
    }

    /**
     * @return boolean
     */
    public boolean isUseForToStringMethod()
    {
        return this.useForToStringMethod;
    }

    /**
     * @param isAssoziation boolean
     */
    public void setAssoziation(final boolean isAssoziation)
    {
        this.isAssoziation = isAssoziation;
    }

    /**
     * @param isCollection boolean
     */
    public void setCollection(final boolean isCollection)
    {
        this.isCollection = isCollection;
    }

    /**
     * @param useForToStringMethod boolean
     */
    public void setUseForToString(final boolean useForToStringMethod)
    {
        this.useForToStringMethod = useForToStringMethod;
    }

    /**
     * @return String
     */
    synchronized Class<?> getFieldClazz()
    {
        if (this.fieldClazz == null)
        {
            try
            {

                this.fieldClazz = ClassUtils.forName(getFieldClazzName(), null);
            }
            catch (ClassNotFoundException ex)
            {
                throw new RuntimeException(ex);
            }
        }

        return this.fieldClazz;
    }
}
