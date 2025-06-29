// Created: 19.12.23
package de.freese.led.encoder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.StringJoiner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * Helper to encode Symbols for a LED-Matrix.
 * Each LED-Row is encoded as an int bitwise.
 *
 * @author Thomas Freese
 */
public final class SymbolEncoderMain {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new SymbolEncoderMain().init(5, 7);
            frame.setVisible(true);
        });
    }

    private JTextArea textArea;
    private JTextField[] textFields;
    private JToggleButton[][] toggleButtons;

    private SymbolEncoderMain() {
        super();
    }

    private JPanel createPanelDots(final int dotsHorizontal, final int dotsVertical) {
        toggleButtons = new JToggleButton[dotsVertical][dotsHorizontal];

        final JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(dotsVertical, dotsHorizontal));

        UIManager.put("ToggleButton.select", Color.GRAY);

        for (int y = 0; y < dotsVertical; y++) {
            for (int x = 0; x < dotsHorizontal; x++) {
                final int row = y;

                final JToggleButton button = new JToggleButton(x + "-" + y);
                button.setPreferredSize(new Dimension(60, 60));
                button.addActionListener(action -> encodeRow(row));
                button.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(final MouseEvent event) {
                        // Empty
                    }

                    @Override
                    public void mouseEntered(final MouseEvent event) {
                        event.consume();

                        if (event.isControlDown()) {
                            button.setSelected(!button.isSelected());
                            encodeRow(row);
                        }
                    }

                    @Override
                    public void mouseExited(final MouseEvent event) {
                        // Empty
                    }

                    @Override
                    public void mousePressed(final MouseEvent event) {
                        // Empty
                    }

                    @Override
                    public void mouseReleased(final MouseEvent event) {
                        // Empty
                    }
                });

                toggleButtons[y][x] = button;
                panel.add(button);
            }
        }

        return panel;
    }

    private JPanel createPanelEncoded(final int dotsHorizontal, final int dotsVertical) {
        textFields = new JTextField[dotsVertical];

        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        for (int y = 0; y < dotsVertical; y++) {
            final int row = y;

            final JTextField textField = new JTextField();
            textField.setPreferredSize(new Dimension(100, 40));
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(final KeyEvent event) {
                    event.consume();

                    try {
                        final int encoded = Integer.parseInt(textField.getText());
                        decodeRow(row, encoded);
                    }
                    catch (NumberFormatException ex) {
                        // Ignore
                    }
                }
            });

            textFields[y] = textField;

            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = y;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(dotsVertical + dotsHorizontal, 0, dotsVertical + dotsHorizontal, 0);
            panel.add(textField, gbc);
        }

        return panel;
    }

    private JTextArea createTextArea() {
        textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(100, 50));
        textArea.setEditable(false);
        textArea.setFont(textArea.getFont().deriveFont(Font.BOLD, 16));

        return textArea;
    }

    private void decodeRow(final int row, final int encoded) {
        for (int x = 0; x < toggleButtons[row].length; x++) {
            final JToggleButton toggleButton = toggleButtons[row][x];

            toggleButton.setSelected((encoded & (1 << x)) != 0);
        }

        updateTextArea();
    }

    private void encodeRow(final int row) {
        // final BitSet bitSet = new BitSet(toggleButtons[row].length);
        int encoded = 0;

        for (int x = 0; x < toggleButtons[row].length; x++) {
            final JToggleButton toggleButton = toggleButtons[row][x];

            if (toggleButton.isSelected()) {
                // bitSet.set(x);
                encoded += 1 << x;
            }
        }

        // final byte[] bytes = bitSet.toByteArray();
        // final int encoded = IntStream.range(0, bytes.length).map(idx -> bytes[idx]).sum();
        // System.out.println("bitSet = " + encoded);

        textFields[row].setText(Integer.toString(encoded));

        updateTextArea();
    }

    private JFrame init(final int dotsHorizontal, final int dotsVertical) {
        final JFrame frame = new JFrame("LED Matrix Encoder");
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = dotsHorizontal;
        gbc.gridheight = dotsVertical;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(createPanelDots(dotsHorizontal, dotsVertical), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = dotsHorizontal;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        frame.add(createPanelEncoded(dotsHorizontal, dotsVertical), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = dotsHorizontal + 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(createTextArea(), gbc);

        final JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(action -> {
            for (final JToggleButton[] toggleButton : toggleButtons) {
                for (final JToggleButton jToggleButton : toggleButton) {
                    jToggleButton.setSelected(false);
                }
            }

            for (final JTextField textField : textFields) {
                textField.setText(null);
            }

            textArea.setText(null);
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = dotsHorizontal;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(clearButton, gbc);

        frame.pack();
        // frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        return frame;
    }

    private void updateTextArea() {
        final StringJoiner stringJoiner = new StringJoiner(", ", "new int[]{", "}");

        for (final JTextField textField : textFields) {
            final String value = textField.getText();

            if (value == null || value.isBlank()) {
                stringJoiner.add("0");
            }
            else {
                stringJoiner.add(value);
            }
        }

        textArea.setText(stringJoiner.toString());
    }
}
