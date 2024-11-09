// Created: 15.11.2020

package de.freese.jconky;

import java.util.List;
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

import de.freese.jconky.painter.CpuMonitorPainter;
import de.freese.jconky.painter.HostMonitorPainter;
import de.freese.jconky.painter.MusicMonitorPainter;
import de.freese.jconky.painter.NetworkMonitorPainter;
import de.freese.jconky.painter.ProcessMonitorPainter;
import de.freese.jconky.painter.SystemMonitorPainter;
import de.freese.jconky.painter.TemperatureMonitorPainter;

/**
 * Mit JConkyLauncher ausführen oder JConky direkt mit folgenden Restriktionen:<br>
 * <br>
 * In Eclipse:<br>
 * <ol>
 * <li>Konstruktor muss public empty-arg sein oder nicht vorhanden sein.</li>
 * <li>VM-Parameter: --add-modules javafx.controls</li>
 * <li>Module-Classpath: OpenJFX die jeweils 2 Jars für javafx-base, javafx-controls und javafx-graphics hinzufügen</li>
 * </ol>
 *
 * @author Thomas Freese
 */
public final class JConky extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(JConky.class);

    public static Logger getLogger() {
        return LOGGER;
    }

    // public static void main(final String[] args) {
    // // Kein Taskbar Icon, funktioniert unter Linux aber nicht.
    // PlatformImpl.setTaskbarApplication(false);
    //
    // // Runtime wird nicht beendet, wenn letztes Fenster geschlossen wird.
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

    private ContextPainter conkyContextPainter;
    private ScheduledExecutorService scheduledExecutorService;

    public Scene createScene() {
        // Font-Antialiasing
        System.setProperty("prism.lcdtext", "true");

        final Canvas canvas = new Canvas();
        this.conkyContextPainter.setCanvas(canvas);

        final Group pane = new Group();
        pane.getChildren().add(canvas);

        // GridPane pane = new GridPane();
        // pane.add(canvas, 0, 0);

        // Scene
        final Scene scene = new Scene(pane, 335D, 1070D, true, SceneAntialiasing.BALANCED);

        // Bind canvas size to scene size.
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        getLogger().info("Antialiasing: {}", scene.getAntiAliasing());

        return scene;
    }

    @Override
    public void init() throws Exception {
        // "JavaFX-Launcher" umbenennen.
        Thread.currentThread().setName("JavaFX-Init");

        getLogger().info("init");

        this.scheduledExecutorService = Executors.newScheduledThreadPool(4);
        this.conkyContextPainter = new ContextPainter();

        this.conkyContextPainter.addMonitorPainter(new HostMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new CpuMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new SystemMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new NetworkMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new ProcessMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new TemperatureMonitorPainter());
        this.conkyContextPainter.addMonitorPainter(new MusicMonitorPainter());

        getScheduledExecutorService().execute(() -> Context.getInstance().updateOneShot());

        // Short-Scheduled
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long delay = 3000L;
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateUptimeInSeconds(), 0L, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateCpuInfos(), 0L, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateNetworkInfos(), 0L, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateUsages(), 0L, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateProcessInfos(), 0L, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateTemperatures(), 0L, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateMusicInfo(), 0L, delay, timeUnit);

        // Long-Scheduled
        timeUnit = TimeUnit.MINUTES;
        delay = 15L;
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateHostInfo(), 0L, delay, timeUnit);
        getScheduledExecutorService().scheduleWithFixedDelay(() -> Context.getInstance().updateUpdates(), 0L, delay, timeUnit);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        // "JavaFX Application Thread" umbenennen.
        Thread.currentThread().setName("JavaFX-Thread");

        getLogger().info("start");

        final Scene scene = createScene();

        // Transparenz
        final boolean isTransparentSupported = Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW);
        // isTransparentSupported = false;

        if (isTransparentSupported) {
            // Fenster wird hierbei undecorated, aber der Content wird normal gezeichnet.

            // For Stage
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            // Das gesamte Fenster wird transparent, inklusive Titelleiste und Inhalt.
            // primaryStage.setOpacity(Settings.getInstance().getAlpha());

            // For Scene
            // scene.setFill(Color.TRANSPARENT);
            scene.setFill(new Color(0D, 0D, 0D, Settings.getInstance().getAlpha()));

            // canvas.setOpacity(Settings.getInstance().getAlpha());

            // Für Container.
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

        // Auf dem 2. Monitor
        final List<Screen> screens = Screen.getScreens();
        // Screen screen = screens.get(0); // Linker Monitor
        final Screen screen = screens.getLast(); // Rechter Monitor
        primaryStage.setX(screen.getVisualBounds().getMinX() + 1240D);
        primaryStage.setY(5D);

        startRepaintSchedule();

        // primaryStage.sizeToScene();
        primaryStage.show();
    }

    public void startRepaintSchedule() {
        getScheduledExecutorService().scheduleWithFixedDelay(() -> {
            try {
                Platform.runLater(this.conkyContextPainter::paint);
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }
        }, 400L, 3000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() throws Exception {
        getLogger().info("stop");

        getScheduledExecutorService().shutdown();

        System.exit(0);
    }

    private ScheduledExecutorService getScheduledExecutorService() {
        return this.scheduledExecutorService;
    }
}
