// Created: 26.04.2012
package de.freese.sonstiges.sound;

import java.io.File;
import java.io.Serial;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * To play sound using Clip, the process need to be alive.<br>
 * Hence, we use a Swing application.
 */
public final class SoundClipMain extends JFrame {
    @Serial
    private static final long serialVersionUID = -4981933991308257211L;

    public static void main(final String[] args) {
        new SoundClipMain();
    }

    private SoundClipMain() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Test Sound Clip");
        this.setSize(300, 200);
        setVisible(true);

        File file = new File("C:/Windows/media/tada.wav");

        try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(file); Clip clip = AudioSystem.getClip()) {
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);

            // Stop the player if it is still running
            if (clip.isRunning()) {
                clip.stop();
            }

            // rewind to the beginning
            clip.setFramePosition(0);

            // play once
            clip.start();

            // repeat none (play once), can be used in place of start().
            // clip.loop(0);

            // repeat 5 times (play 6 times)
            // clip.loop(5);

            // repeat forever
            // clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        // dispose();
        // System.exit(0);
    }
}
