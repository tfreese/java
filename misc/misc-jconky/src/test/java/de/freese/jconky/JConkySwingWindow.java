// Created: 25.12.2020
package de.freese.jconky;

import java.awt.Color;
import java.awt.Window.Type;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

/**
 * @author Thomas Freese
 */
public class JConkySwingWindow
{
    public static void main(final String[] args)
    {
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame();
        frame.setType(Type.POPUP);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBackground(new Color(0, 0, 0, 0));
        // frame.setBackground(Color.BLACK);
        frame.setSize(335, 1070);
        frame.setLocationRelativeTo(null);
        // frame.setOpacity((float) Settings.getInstance().getAlpha());

        JFXPanel fxPanel = new JFXPanel();
        fxPanel.setBackground(new Color(0, 0, 0, 0));
        // fxPanel.setBackground(Color.BLACK);
        frame.setContentPane(fxPanel);

        Platform.runLater(() ->
        {
            JConky jConky = new JConky();

            try
            {
                jConky.init();
            }
            catch (Exception ex)
            {
                JConky.getLogger().error(ex.getMessage(), ex);
            }

            Scene scene = jConky.createScene();
            scene.setFill(new javafx.scene.paint.Color(0D, 0D, 0D, Settings.getInstance().getAlpha()));
            fxPanel.setScene(scene);

            SwingUtilities.invokeLater(() ->
            {
                jConky.startRepaintSchedule();
                frame.setVisible(true);
            });
        });
    }
}
