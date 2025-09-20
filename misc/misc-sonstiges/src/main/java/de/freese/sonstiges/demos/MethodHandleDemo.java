// Created: 24.04.2017
package de.freese.sonstiges.demos;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class MethodHandleDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandleDemo.class);

    /**
     * @author Thomas Freese
     */
    static class MyPoint {
        private int x;
        private int y;
        private int z;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public void setX(final int x) {
            this.x = x;
        }

        public void setY(final int y) {
            this.y = y;
        }

        public void setZ(final int z) {
            this.z = z;
        }
    }

    static void main() throws Throwable {
        accessFields();
        accessPrivateFields();
        insertArguments();

        // MethodHandle mh = MethodHandles.throwException(Void.class, SQLException.class);
        // mh.invoke(new SQLException("test"));
    }

    private static void accessFields() throws Throwable {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final MyPoint point = new MyPoint();

        // Set the x and y fields.
        MethodHandle mh = lookup.findSetter(MyPoint.class, "x", int.class);
        mh.invoke(point, 15);

        mh = lookup.findSetter(MyPoint.class, "y", int.class);
        mh.invoke(point, 30);

        // Get the field values.
        mh = lookup.findGetter(MyPoint.class, "x", int.class);
        final int x = (int) mh.invoke(point);
        LOGGER.info("x = {}", x);

        mh = lookup.findGetter(MyPoint.class, "y", int.class);
        final int y = (int) mh.invoke(point);
        LOGGER.info("y = {}", y);
    }

    private static void accessPrivateFields() throws Throwable {
        final Field field = MyPoint.class.getDeclaredField("z");
        field.setAccessible(true);

        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final MethodHandle mhSetter = lookup.unreflectSetter(field);

        final MyPoint point = new MyPoint();

        // field.set(point, 5);
        // field.get(point);
        mhSetter.invoke(point, 5);

        final MethodHandle mhGetter = lookup.unreflectGetter(field);
        final int z = (int) mhGetter.invoke(point);

        LOGGER.info("z = {}", z);
    }

    private static void insertArguments() throws Throwable {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        MethodHandle mh = lookup.findStatic(Math.class, "pow", MethodType.methodType(double.class, double.class, double.class));
        LOGGER.info("2^10 = {}", (double) mh.invoke(2.0, 10.0D));

        // Vordefinition des 2. Parameters.
        mh = MethodHandles.insertArguments(mh, 1, 10);
        LOGGER.info("2^10 = {}", (double) mh.invoke(2.0D));
    }

    private MethodHandleDemo() {
        super();
    }
}
