// Created: 01.06.2017
package de.freese.jsensors.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class Utils {
    public static final String[] EMPTY_STRING_ARRAY = {};

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static List<String> executeCommand(final List<String> command) {
        List<String> list = null;

        try {
            // @formatter:off
            Process process = new ProcessBuilder()
                    .command(command)
                    .redirectErrorStream(true)
                    .start();
            // @formatter:on

            Charset charset = StandardCharsets.UTF_8;

            // try (InputStreamReader isr = new InputStreamReader(process.getInputStream()))
            // {
            // System.out.println(isr.getEncoding());
            // }
            try (BufferedReader readerIn = new BufferedReader(new InputStreamReader(process.getInputStream(), charset))) {
                list = readerIn.lines().filter(l -> !l.isEmpty()).toList();
            }

            // list.forEach(System.out::println);

            process.waitFor();
            process.destroy();
        }
        catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return list;
    }

    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    public static boolean isWindows() {
        return OS.startsWith("win");
    }

    public static String stripWhitespaces(final String value) {
        if (value == null) {
            return null;
        }

        if (value.isBlank()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        value.chars().filter(c -> !Character.isWhitespace((char) c)).forEach(c -> sb.append((char) c));

        return sb.toString();
    }

    private Utils() {
        super();
    }
}
