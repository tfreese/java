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

/**
 * @author Thomas Freese
 */
public final class SingleNoteSynthesizerMain {
    public static void main(final String[] args) {
        new SingleNoteSynthesizerMain().playNote(60);
    }

    private final ShortMessage message = new ShortMessage();

    private Receiver receiver;

    private Synthesizer synth;

    private SingleNoteSynthesizerMain() {
        super();

        try {
            this.synth = MidiSystem.getSynthesizer();
            this.synth.open();
            this.receiver = this.synth.getReceiver();
        }
        catch (MidiUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    public void listAvailableInstruments() {
        Instrument[] instrument = this.synth.getAvailableInstruments();

        for (int i = 0; i < instrument.length; i++) {
            System.out.println(i + "   " + instrument[i].getName());
        }
    }

    public void playNote(final int note) {
        setShortMessage(note, ShortMessage.NOTE_ON);
        this.receiver.send(this.message, -1);

        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();

            Thread.currentThread().interrupt();
        }

        setShortMessage(note, ShortMessage.NOTE_OFF);
        this.receiver.send(this.message, -1);
    }

    private void setShortMessage(final int note, final int onOrOff) {
        try {
            this.message.setMessage(onOrOff, 0, note, 70);
        }
        catch (InvalidMidiDataException ex) {
            ex.printStackTrace();
        }
    }
}
