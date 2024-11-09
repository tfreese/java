// Created: 07.08.2003
package de.freese.sonstiges.sound.midi;

import java.util.concurrent.TimeUnit;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class SingleNoteChannelMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleNoteChannelMain.class);

    public static void main(final String[] args) {
        playNote(60);
    }

    public static void playNote(final int note) {
        try (Synthesizer synth = MidiSystem.getSynthesizer()) {
            synth.open();

            final MidiChannel channel = synth.getChannels()[0];
            channel.noteOn(note, 70);

            TimeUnit.MILLISECONDS.sleep(1000);

            channel.noteOff(note, 70);
        }
        catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        System.exit(0);
    }

    private SingleNoteChannelMain() {
        super();
    }
}
