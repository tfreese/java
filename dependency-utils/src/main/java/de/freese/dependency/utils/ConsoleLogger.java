package de.freese.dependency.utils;

import java.io.PrintStream;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;

/**
 * @author Thomas Freese
 * @since 08.07.26
 */
@SuppressWarnings({"OverridingMethodInconsistentArgumentNamesChecker"})
public final class ConsoleLogger implements Logger {
    // Plugin "Grep Console" will override these AnsiCodes!
    //
    // RED = \u001B[31m
    // GREEN = \u001B[32m
    // CYAN = \u001B[36m
    // ORANGE = \u001B[38;5;208m
    // GRAY = \u001B[90m
    //
    // "\033" (hex)
    // "\u001B"
    private static final String ANSI_ESC = String.valueOf((char) 27);

    // Orange (True Color, 24-bit): RGB 0,255,255
    private static final String ANSI_CYAN = ANSI_ESC + "[38;2;0;255;255m";

    // Grau (True Color, 24-bit): RGB 128,128,128
    private static final String ANSI_GRAY = ANSI_ESC + "[38;2;128;128;128m";

    // Orange (True Color, 24-bit): RGB 255,165,0
    private static final String ANSI_ORANGE = ANSI_ESC + "[38;2;255;165;0m";

    private static final String ANSI_RESET = ANSI_ESC + "[0m";

    public static Logger of(final String name) {
        return new ConsoleLogger(name, System.out, System.err);
    }

    public static Logger of(final Class<?> clazz) {
        return of(Objects.requireNonNull(clazz, "clazz required").getSimpleName());
    }

    private final PrintStream err;
    private final String name;
    private final PrintStream out;

    private ConsoleLogger(final String name, final PrintStream out, final PrintStream err) {
        super();

        this.name = Objects.requireNonNull(name, "name required");
        this.out = Objects.requireNonNull(out, "out out required");
        this.err = Objects.requireNonNull(err, "err required");
    }

    @Override
    public void debug(final String msg) {
        log(Level.DEBUG, msg);
    }

    @Override
    public void debug(final String format, final Object arg) {
        log(Level.DEBUG, format, arg);
    }

    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {
        log(Level.DEBUG, format, arg1, arg2);
    }

    @Override
    public void debug(final String format, final Object... arguments) {
        log(Level.DEBUG, format, arguments);
    }

    @Override
    public void debug(final String msg, final Throwable t) {
        log(Level.DEBUG, msg, t);
    }

    @Override
    public void debug(final Marker marker, final String msg) {
        log(Level.DEBUG, marker, msg);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void debug(final Marker marker, final String format, final Object... arguments) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void debug(final Marker marker, final String msg, final Throwable t) {
        log(Level.DEBUG, marker, msg, t);
    }

    @Override
    public void error(final String msg) {
        log(Level.ERROR, msg);
    }

    @Override
    public void error(final String format, final Object arg) {
        log(Level.ERROR, format, arg);
    }

    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        log(Level.ERROR, format, arg1, arg2);
    }

    @Override
    public void error(final String format, final Object... arguments) {
        log(Level.ERROR, format, arguments);
    }

    @Override
    public void error(final String msg, final Throwable t) {
        log(Level.ERROR, msg, t);
    }

    @Override
    public void error(final Marker marker, final String msg) {
        log(Level.ERROR, marker, msg);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void error(final Marker marker, final String format, final Object... arguments) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void error(final Marker marker, final String msg, final Throwable t) {
        log(Level.ERROR, marker, msg, t);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void info(final String msg) {
        log(Level.INFO, msg);
    }

    @Override
    public void info(final String format, final Object arg) {
        log(Level.INFO, format, arg);
    }

    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        log(Level.INFO, format, arg1, arg2);
    }

    @Override
    public void info(final String format, final Object... arguments) {
        log(Level.INFO, format, arguments);
    }

    @Override
    public void info(final String msg, final Throwable t) {
        log(Level.INFO, msg, t);
    }

    @Override
    public void info(final Marker marker, final String msg) {
        log(Level.INFO, marker, msg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void info(final Marker marker, final String format, final Object... arguments) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void info(final Marker marker, final String msg, final Throwable t) {
        log(Level.INFO, marker, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return true;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return true;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return true;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return true;
    }

    @Override
    public void trace(final String msg) {
        log(Level.TRACE, msg);
    }

    @Override
    public void trace(final String format, final Object arg) {
        log(Level.TRACE, format, arg);
    }

    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {
        log(Level.TRACE, format, arg1, arg2);
    }

    @Override
    public void trace(final String format, final Object... arguments) {
        log(Level.TRACE, format, arguments);
    }

    @Override
    public void trace(final String msg, final Throwable t) {
        log(Level.TRACE, msg, t);
    }

    @Override
    public void trace(final Marker marker, final String msg) {
        log(Level.TRACE, marker, msg);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void trace(final Marker marker, final String format, final Object... arguments) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void trace(final Marker marker, final String msg, final Throwable t) {
        log(Level.TRACE, marker, msg, t);
    }

    @Override
    public void warn(final String msg) {
        log(Level.WARN, msg);
    }

    @Override
    public void warn(final String format, final Object arg) {
        log(Level.WARN, format, arg);
    }

    @Override
    public void warn(final String format, final Object... arguments) {
        log(Level.WARN, format, arguments);
    }

    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        log(Level.WARN, format, arg1, arg2);
    }

    @Override
    public void warn(final String msg, final Throwable t) {
        log(Level.WARN, msg, t);
    }

    @Override
    public void warn(final Marker marker, final String msg) {
        log(Level.WARN, marker, msg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void warn(final Marker marker, final String format, final Object... arguments) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void warn(final Marker marker, final String msg, final Throwable t) {
        log(Level.WARN, marker, msg, t);
    }

    private String formatToString(final String format, final Object... arguments) {
        if (arguments.length == 0) {
            return format;
        }

        final StringBuilder sb = new StringBuilder();

        int argumentIndex = 0;
        int placeHolderIndex;
        int lastPlaceHolderIndex = 0;

        while ((placeHolderIndex = format.indexOf("{}", lastPlaceHolderIndex)) >= 0) {
            sb.append(format, lastPlaceHolderIndex, placeHolderIndex);

            if (argumentIndex < arguments.length) {
                sb.append(arguments[argumentIndex++]);
            } else {
                sb.append("{}");
            }

            lastPlaceHolderIndex = placeHolderIndex + 2;
        }

        sb.append(format, lastPlaceHolderIndex, format.length());

        return sb.toString();
    }

    private void log(final Level level, final String format, final Object arg1, final Object arg2) {
        final String message = formatToString(format, arg1, arg2);

        log(level, message);
    }

    private void log(final Level level, final String format, final Object arg) {
        final String message = formatToString(format, arg);

        log(level, message);
    }

    private void log(final Level level, final String format, final Object... arguments) {
        final String message = formatToString(format, arguments);

        log(level, message);
    }

    private void log(final Level level, final Marker marker, final String msg, final Throwable t) {
        final String message = marker.getName() + " - " + msg + " - " + t;

        log(level, message);
    }

    private void log(final Level level, final String msg, final Throwable t) {
        final String message = msg + " - " + t;

        log(level, message);
    }

    /**
     * Plugin "Grep Console" will override these AnsiCodes!
     */
    private void log(final Level level, final String message) {
        final String logMessage = message;
        // final String logMessage = level + " - " + message;

        if (Level.DEBUG.equals(level)) {
            out.println(ANSI_CYAN + logMessage + ANSI_RESET);
        } else if (Level.ERROR.equals(level)) {
            err.println(logMessage);
            // out.println(ANSI_RED +  logMessage + ANSI_RESET);
        } else if (Level.INFO.equals(level)) {
            out.println(logMessage);
        } else if (Level.TRACE.equals(level)) {
            out.println(ANSI_GRAY + logMessage + ANSI_RESET);
        } else if (Level.WARN.equals(level)) {
            out.println(ANSI_ORANGE + logMessage + ANSI_RESET);
        }

        // out.flush();
        // err.flush();
    }

    private void log(final Level level, final Marker marker, final String msg) {
        final String message = marker.getName() + " - " + msg;

        log(level, message);
    }
}
