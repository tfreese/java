// Created: 07.08.2003
package de.freese.sonstiges.sound.midi;

import java.util.concurrent.TimeUnit;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

/**
 * @author Thomas Freese
 */
public class SingleNoteSynthesizer2
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        SingleNoteSynthesizer2 synth = new SingleNoteSynthesizer2();
        synth.setInstrument(19);
        synth.playMajorChord(60);
    }

    /**
     *
     */
    private final ShortMessage message = new ShortMessage();
    /**
     *
     */
    private Receiver receiver;
    /**
     *
     */
    private Synthesizer synth;

    /**
     * Creates a new {@link SingleNoteSynthesizer2} object.
     */
    public SingleNoteSynthesizer2()
    {
        super();

        try
        {
            this.synth = MidiSystem.getSynthesizer();
            this.synth.open();
            this.receiver = this.synth.getReceiver();
        }
        catch (MidiUnavailableException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * @param baseNote int
     */
    public void playMajorChord(final int baseNote)
    {
        playNote(baseNote, 1000);
        playNote(baseNote + 4, 1000);
        playNote(baseNote + 7, 1000);
        startNote(baseNote);
        startNote(baseNote + 4);
        playNote(baseNote + 7, 2000);
        stopNote(baseNote + 4);
        stopNote(baseNote);
    }

    /**
     * @param note int
     * @param duration int
     */
    public void playNote(final int note, final int duration)
    {
        startNote(note);

        try
        {
            TimeUnit.MILLISECONDS.sleep(duration);
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();

            Thread.currentThread().interrupt();
        }

        stopNote(note);
    }

    /**
     * @param instrument int
     */
    public void setInstrument(final int instrument)
    {
        this.synth.getChannels()[0].programChange(instrument);
    }

    /**
     * @param note int
     */
    public void startNote(final int note)
    {
        setShortMessage(ShortMessage.NOTE_ON, note);
        this.receiver.send(this.message, -1);
    }

    /**
     * @param note int
     */
    public void stopNote(final int note)
    {
        setShortMessage(ShortMessage.NOTE_OFF, note);
        this.receiver.send(this.message, -1);
    }

    /**
     * @param onOrOff int
     * @param note int
     */
    private void setShortMessage(final int onOrOff, final int note)
    {
        try
        {
            this.message.setMessage(onOrOff, 0, note, 70);
        }
        catch (InvalidMidiDataException ex)
        {
            ex.printStackTrace();
        }
    }
}
