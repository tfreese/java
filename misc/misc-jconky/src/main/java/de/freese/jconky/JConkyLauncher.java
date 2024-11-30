// Created: 24.12.2020
package de.freese.jconky;

import java.text.NumberFormat;
import java.util.Arrays;

import javafx.application.Application;

import com.sun.javafx.application.PlatformImpl;

/**
 * @author Thomas Freese
 */
public final class JConkyLauncher {
    public static void main(final String[] args) {
        if (System.getProperty("LOG_DIR") == null) {
            // Docker kompatibel machen.
            System.setProperty("LOG_DIR", System.getProperty("user.home") + "/.java-apps/jconky");
        }

        // Kein Taskbar Icon, funktioniert unter Linux aber nicht.
        PlatformImpl.setTaskbarApplication(false);

        // Runtime wird nicht beendet, wenn letztes Fenster geschlossen wird.
        // Platform.setImplicitExit(false);

        // System.setProperty("apple.awt.UIElement", "true");
        // System.setProperty("apple.awt.headless", "true");
        // System.setProperty("java.awt.headless", "true");
        // System.setProperty("javafx.macosx.embedded", "true");
        // java.awt.Toolkit.getDefaultToolkit();

        dumpSystemInfo(args);

        Application.launch(JConky.class, args);
    }

    private static void dumpSystemInfo(final String[] args) {
        final Runtime runtime = Runtime.getRuntime();

        final NumberFormat format = NumberFormat.getInstance();

        final long maxMemory = runtime.maxMemory();
        final long allocatedMemory = runtime.totalMemory();
        final long freeMemory = runtime.freeMemory();
        final long divider = 1024L * 1024L;
        final String unit = "MB";

        JConky.getLogger().info("========================== System Info ==========================");
        JConky.getLogger().info("System: {}/{} - {}", System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"));
        JConky.getLogger().info("User-Dir: {}", System.getProperty("user.dir"));
        JConky.getLogger().info("Log-Dir: {}", System.getProperty("LOG_DIR"));
        JConky.getLogger().info("Programm-Args: {}", Arrays.toString(args));
        JConky.getLogger().info("CPU Cores: {}", runtime.availableProcessors());
        JConky.getLogger().info("Free memory: {}", format.format(freeMemory / divider) + unit);
        JConky.getLogger().info("Allocated memory: {}", format.format(allocatedMemory / divider) + unit);
        JConky.getLogger().info("Max memory: {}", format.format(maxMemory / divider) + unit);
        JConky.getLogger().info("Total free memory: {}", format.format((freeMemory + (maxMemory - allocatedMemory)) / divider) + unit);
        JConky.getLogger().info("=================================================================");

        // try (PrintWriter pw = new PrintWriter(new FileOutputStream("/jconky/logs/application.log", true), true, StandardCharsets.UTF_8)) {
        // pw.println();
        // pw.print("Test");
        // }
        // catch (FileNotFoundException ex) {
        // ex.printStackTrace();
        // }
    }

    private JConkyLauncher() {
        super();
    }
}
