package de.freese.sonstiges.sound.midi;

import java.util.concurrent.TimeUnit;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 * @since 07.08.2003
 */
public final class SingleNoteChannelMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleNoteChannelMain.class);

    public static void playNote(final int note) {
        try (Synthesizer synth = MidiSystem.getSynthesizer()) {
            synth.open();

            final MidiChannel channel = synth.getChannels()[0];
            channel.noteOn(note, 70);

            TimeUnit.MILLISECONDS.sleep(1000L);

            channel.noteOff(note, 70);
        }
        catch (final InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (final Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        System.exit(0);
    }

    static void main() {
        playNote(60);
    }

    private SingleNoteChannelMain() {
        super();
    }
}
