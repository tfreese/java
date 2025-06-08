// Created: 07.08.2003
package de.freese.sonstiges.sound.midi;

import java.util.concurrent.TimeUnit;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class SequencerSoundMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(SequencerSoundMain.class);

    public static void main(final String[] args) throws Exception {
        new SequencerSoundMain();

        TimeUnit.MILLISECONDS.sleep(8000);

        System.exit(0);
    }

    private Sequence sequence;
    private Sequencer sequencer;
    private Track track;

    private SequencerSoundMain() {
        super();

        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
        }
        catch (MidiUnavailableException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        createTrack();
        makeScale(20);
        startSequencer();
    }

    public void makeScale(final int baseNote) {
        for (int i = 0; i < 13; i++) {
            startNote(baseNote + i, i);
            stopNote(baseNote + i, i + 1);
            startNote(baseNote + i, 25 - i);
            stopNote(baseNote + i, 26 - i);
        }
    }

    public void startNote(final int note, final int tick) {
        setShortMessage(ShortMessage.NOTE_ON, note, tick);
    }

    public void stopNote(final int note, final int tick) {
        setShortMessage(ShortMessage.NOTE_OFF, note, tick);
    }

    private void createTrack() {
        try {
            sequence = new Sequence(Sequence.PPQ, 4);
        }
        catch (InvalidMidiDataException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        track = sequence.createTrack();
    }

    private void setShortMessage(final int onOrOff, final int note, final int tick) {
        final ShortMessage message = new ShortMessage();

        try {
            message.setMessage(onOrOff, 0, note, 90);

            final MidiEvent event = new MidiEvent(message, tick);
            track.add(event);
        }
        catch (InvalidMidiDataException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void startSequencer() {
        try {
            sequencer.setSequence(sequence);
        }
        catch (InvalidMidiDataException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        sequencer.start();
        sequencer.setTempoInBPM(60);
    }
}
