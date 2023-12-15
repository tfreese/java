// Created: 07.08.2003
package de.freese.sonstiges.sound.midi;

import java.util.concurrent.TimeUnit;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

/**
 * @author Thomas Freese
 */
public final class SingleNoteChannelMain {
    public static void main(final String[] args) {
        new SingleNoteChannelMain().playNote(60);
    }

    private SingleNoteChannelMain() {
        super();
    }

    public void playNote(final int note) {
        try (Synthesizer synth = MidiSystem.getSynthesizer()) {
            synth.open();

            final MidiChannel channel = synth.getChannels()[0];
            channel.noteOn(note, 70);

            TimeUnit.MILLISECONDS.sleep(1000);

            channel.noteOff(note, 70);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }
}
