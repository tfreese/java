// Created: 24.04.2017
package de.freese.sonstiges.demos;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

/**
 * @author Thomas Freese
 */
public final class MethodHandleMain
{
    /**
     * @author Thomas Freese
     */
    static class MyPoint
    {
        private int x;

        private int y;

        private int z;

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public int getZ()
        {
            return z;
        }

        public void setX(final int x)
        {
            this.x = x;
        }

        public void setY(final int y)
        {
            this.y = y;
        }

        public void setZ(final int z)
        {
            this.z = z;
        }
    }

    public static void main(final String[] args) throws Throwable
    {
        accessFields();
        accessPrivateFields();
        insertArguments();

        // MethodHandle mh = MethodHandles.throwException(Void.class, SQLException.class);
        // mh.invoke(new SQLException("test"));
    }

    private static void accessFields() throws Throwable
    {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MyPoint point = new MyPoint();

        // Set the x and y fields.
        MethodHandle mh = lookup.findSetter(MyPoint.class, "x", int.class);
        mh.invoke(point, 15);

        mh = lookup.findSetter(MyPoint.class, "y", int.class);
        mh.invoke(point, 30);

        // Get the field values.
        mh = lookup.findGetter(MyPoint.class, "x", int.class);
        int x = (int) mh.invoke(point);
        System.out.printf("x = %d%n", x);

        mh = lookup.findGetter(MyPoint.class, "y", int.class);
        int y = (int) mh.invoke(point);
        System.out.printf("y = %d%n", y);
    }

    private static void accessPrivateFields() throws Throwable
    {
        Field field = MyPoint.class.getDeclaredField("z");
        field.setAccessible(true);

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle mhSetter = lookup.unreflectSetter(field);

        MyPoint point = new MyPoint();

        // field.set(point, 5);
        // field.get(point);
        mhSetter.invoke(point, 5);

        MethodHandle mhGetter = lookup.unreflectGetter(field);
        int z = (int) mhGetter.invoke(point);

        System.out.printf("z = %d%n", z);
    }

    private static void insertArguments() throws Throwable
    {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        MethodHandle mh = lookup.findStatic(Math.class, "pow", MethodType.methodType(double.class, double.class, double.class));
        System.out.printf("2^10 = %f%n", mh.invoke(2.0, 10.0D));

        // Vordefinition des 2. Parameters.
        mh = MethodHandles.insertArguments(mh, 1, 10);
        System.out.printf("2^10 = %f%n", mh.invoke(2.0D));
    }

    private MethodHandleMain()
    {
        super();
    }
}
