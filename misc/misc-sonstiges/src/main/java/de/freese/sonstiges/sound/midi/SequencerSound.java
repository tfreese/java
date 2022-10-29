// Created: 07.08.2003
package de.freese.sonstiges.sound.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * @author Thomas Freese
 */
public class SequencerSound
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        new SequencerSound();
    }

    /**
     *
     */
    private Sequence sequence;
    /**
     *
     */
    private Sequencer sequencer;
    /**
     *
     */
    private Track track;

    /**
     * Creates a new {@link SequencerSound} object.
     */
    public SequencerSound()
    {
        super();

        try
        {
            this.sequencer = MidiSystem.getSequencer();
            this.sequencer.open();
        }
        catch (MidiUnavailableException ex)
        {
            ex.printStackTrace();
        }

        createTrack();
        makeScale(20);
        startSequencer();
    }

    /**
     *
     */
    private void createTrack()
    {
        try
        {
            this.sequence = new Sequence(Sequence.PPQ, 4);
        }
        catch (InvalidMidiDataException ex)
        {
            ex.printStackTrace();
        }

        this.track = this.sequence.createTrack();
    }

    /**
     * @param baseNote int
     */
    public void makeScale(final int baseNote)
    {
        for (int i = 0; i < 13; i++)
        {
            startNote(baseNote + i, i);
            stopNote(baseNote + i, i + 1);
            startNote(baseNote + i, 25 - i);
            stopNote(baseNote + i, 26 - i);
        }
    }

    /**
     * @param onOrOff int
     * @param note int
     * @param tick int
     */
    private void setShortMessage(final int onOrOff, final int note, final int tick)
    {
        ShortMessage message = new ShortMessage();

        try
        {
            message.setMessage(onOrOff, 0, note, 90);

            MidiEvent event = new MidiEvent(message, tick);
            this.track.add(event);
        }
        catch (InvalidMidiDataException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * @param note int
     * @param tick int
     */
    public void startNote(final int note, final int tick)
    {
        setShortMessage(ShortMessage.NOTE_ON, note, tick);
    }

    /**
     *
     */
    private void startSequencer()
    {
        try
        {
            this.sequencer.setSequence(this.sequence);
        }
        catch (InvalidMidiDataException ex)
        {
            ex.printStackTrace();
        }

        this.sequencer.start();
        this.sequencer.setTempoInBPM(60);
    }

    /**
     * @param note int
     * @param tick int
     */
    public void stopNote(final int note, final int tick)
    {
        setShortMessage(ShortMessage.NOTE_OFF, note, tick);
    }
}
