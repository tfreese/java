package de.freese.jconky;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.jconky.sensor.SensorPainters;
import de.freese.jconky.sensor.cpu.CpuPainter;
import de.freese.jconky.sensor.cpu.CpuSensor;
import de.freese.jconky.sensor.host.HostPainter;
import de.freese.jconky.sensor.host.HostSensor;
import de.freese.jconky.sensor.system.SystemPainter;
import de.freese.jconky.sensor.system.SystemSensor;

/**
 * Execute with JConkyLauncher or JConky with the following restrictions:<br>
 * <br>
 * In Eclipse:<br>
 * <ol>
 * <li>Constructor must be public with empty-arg, or not exiting.</li>
 * <li>VM-Parameter: --add-modules javafx.controls</li>
 * <li>Module-Classpath: Add the two 2 Jars for javafx-base, javafx-controls and javafx-graphics</li>
 * </ol>
 *
 * @author Thomas Freese
 * @since 15.11.2020
 */
public final class JConky extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(JConky.class);

    public static Logger getLogger() {
        return LOGGER;
    }

    // static void main() {
    // // No Taskbar Icon, but doesn't work with Linux.
    // PlatformImpl.setTaskbarApplication(false);
    //
    // // Runtime won't be exited, if last Window was closed.
    // // Platform.setImplicitExit(false);
    //
    // // System.setProperty("apple.awt.UIElement", "true");
    // // System.setProperty("apple.awt.headless", "true");
    // // System.setProperty("java.awt.headless", "true");
    // // System.setProperty("javafx.macosx.embedded", "true");
    // // java.awt.Toolkit.getDefaultToolkit();
    //
    // launch(args);
    // }

    private ScheduledExecutorService scheduledExecutorService;
    private SensorPainters sensorPainter;

    @Override
    public void init() {
        // Rename "JavaFX-Launcher".
        Thread.currentThread().setName("JavaFX-Init");

        getLogger().info("init");

        scheduledExecutorService = Executors.newScheduledThreadPool(4);
        sensorPainter = new SensorPainters();

        final HostSensor hostSensor = new HostSensor();
        final CpuSensor cpuSensor = new CpuSensor();
        final SystemSensor systemSensor = new SystemSensor();

        sensorPainter
                .addSensorPainter(new HostPainter(hostSensor))
                .addSensorPainter(new CpuPainter(cpuSensor))
                .addSensorPainter(new SystemPainter(systemSensor))
        ;

        scheduledExecutorService.scheduleWithFixedDelay(hostSensor::update, 0L, 3L, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(cpuSensor::update, 0L, 3L, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(systemSensor::update, 0L, 3L, TimeUnit.SECONDS);
    }

    @Override
    public void start(final Stage primaryStage) {
        // Rename "JavaFX Application Thread".
        Thread.currentThread().setName("JavaFX-Thread");

        getLogger().info("start");

        final Scene scene = createScene();

        final boolean isTransparentSupported = Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW);
        // isTransparentSupported = false;

        if (isTransparentSupported) {
            // Window will be undecorated.

            // For Stage
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            // Window will be transparent.
            // primaryStage.setOpacity(Settings.getInstance().getAlpha());

            // For Scene
            // scene.setFill(Color.TRANSPARENT);
            scene.setFill(new Color(0D, 0D, 0D, Settings.getInstance().getAlpha()));

            // canvas.setOpacity(Settings.getInstance().getAlpha());

            // For Container.
            // pane.setBackground(Background.EMPTY);
            // pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            // pane.setStyle("-fx-background-color: transparent;");
        }
        else {
            scene.setFill(Color.BLACK);
        }

        primaryStage.setTitle("jConky");
        primaryStage.getIcons().add(new Image("conky.png"));
        primaryStage.setScene(scene);

        primaryStage.show();

        Platform.runLater(() -> {
            try {
                primaryStage.sizeToScene();

                final double y = 5D;

                // Right on the 1. Monitor.
                // final double displayWith = Screen.getPrimary().getVisualBounds().getMaxX();

                // Left on the 2. Monitor.
                final double displayWith = Screen.getPrimary().getVisualBounds().getMaxX() + primaryStage.getWidth() + 10D;

                // Right on the 2. Monitor.
                // final double displayWith = Screen.getScreens().stream().map(Screen::getVisualBounds).mapToDouble(Rectangle2D::getWidth).sum();

                double x = displayWith - primaryStage.getWidth() - 5D;

                if (x < 0) {
                    x = 0;
                }

                primaryStage.setX(x);
                primaryStage.setY(y);
            }
            catch (final Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }
        });

        startRepaintSchedule();
    }

    public void startRepaintSchedule() {
        getScheduledExecutorService().scheduleWithFixedDelay(() -> {
            try {
                Platform.runLater(sensorPainter::repaint);
            }
            catch (final Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }
        }, 400L, 3000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        getLogger().info("stop");

        getScheduledExecutorService().shutdown();

        System.exit(0);
    }

    Scene createScene() {
        // Font-Antialiasing
        System.setProperty("prism.lcdtext", "true");

        final Canvas canvas = new Canvas();
        sensorPainter.setCanvas(canvas);

        final Group pane = new Group();
        pane.getChildren().add(canvas);

        // GridPane pane = new GridPane();
        // pane.add(canvas, 0, 0);

        // Scene
        final Scene scene = new Scene(pane, getSettings().getWidth(), getSettings().getHeight(), true, SceneAntialiasing.BALANCED);

        // Bind canvas size to scene size.
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        getLogger().info("Antialiasing: {}", scene.getAntiAliasing());

        return scene;
    }

    private ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    private Settings getSettings() {
        return Settings.getInstance();
    }
}
