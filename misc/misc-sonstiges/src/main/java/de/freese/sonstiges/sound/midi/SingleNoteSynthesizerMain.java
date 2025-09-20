// Created: 07.08.2003
package de.freese.sonstiges.sound.midi;

import java.util.concurrent.TimeUnit;

import javax.sound.midi.Instrument;
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
public final class SingleNoteSynthesizerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleNoteSynthesizerMain.class);

    static void main() {
        new SingleNoteSynthesizerMain().playNote(60);
    }

    private final ShortMessage message = new ShortMessage();

    private Receiver receiver;
    private Synthesizer synth;

    private SingleNoteSynthesizerMain() {
        super();

        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            receiver = synth.getReceiver();
        }
        catch (MidiUnavailableException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public void listAvailableInstruments() {
        final Instrument[] instrument = synth.getAvailableInstruments();

        for (int i = 0; i < instrument.length; i++) {
            LOGGER.info("{}   {}", i, instrument[i].getName());
        }
    }

    public void playNote(final int note) {
        setShortMessage(note, ShortMessage.NOTE_ON);
        receiver.send(message, -1);

        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        }
        catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }

        setShortMessage(note, ShortMessage.NOTE_OFF);
        receiver.send(message, -1);
    }

    private void setShortMessage(final int note, final int onOrOff) {
        try {
            message.setMessage(onOrOff, 0, note, 70);
        }
        catch (InvalidMidiDataException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
