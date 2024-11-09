// Created: 07.08.2003
package de.freese.sonstiges.sound.midi;

import java.util.concurrent.TimeUnit;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class SingleNoteSynthesizer2Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleNoteSynthesizer2Main.class);

    public static void main(final String[] args) {
        final SingleNoteSynthesizer2Main synth = new SingleNoteSynthesizer2Main();
        synth.setInstrument(19);
        synth.playMajorChord(60);
    }

    private final ShortMessage message = new ShortMessage();

    private Receiver receiver;
    private Synthesizer synth;

    private SingleNoteSynthesizer2Main() {
        super();

        try {
            this.synth = MidiSystem.getSynthesizer();
            this.synth.open();
            this.receiver = this.synth.getReceiver();
        }
        catch (MidiUnavailableException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public void playMajorChord(final int baseNote) {
        playNote(baseNote, 1000);
        playNote(baseNote + 4, 1000);
        playNote(baseNote + 7, 1000);
        startNote(baseNote);
        startNote(baseNote + 4);
        playNote(baseNote + 7, 2000);
        stopNote(baseNote + 4);
        stopNote(baseNote);
    }

    public void playNote(final int note, final int duration) {
        startNote(note);

        try {
            TimeUnit.MILLISECONDS.sleep(duration);
        }
        catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);

            Thread.currentThread().interrupt();
        }

        stopNote(note);
    }

    public void setInstrument(final int instrument) {
        this.synth.getChannels()[0].programChange(instrument);
    }

    public void startNote(final int note) {
        setShortMessage(ShortMessage.NOTE_ON, note);
        this.receiver.send(this.message, -1);
    }

    public void stopNote(final int note) {
        setShortMessage(ShortMessage.NOTE_OFF, note);
        this.receiver.send(this.message, -1);
    }

    private void setShortMessage(final int onOrOff, final int note) {
        try {
            this.message.setMessage(onOrOff, 0, note, 70);
        }
        catch (InvalidMidiDataException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
