package net.leddisplay.demo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.io.Serial;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.led.elements.TextDisplayElement;
import net.leddisplay.LedDisplay;
import net.leddisplay.LedDisplayFactory;

/**
 * @author Thomas Freese
 */
public class LedDialog extends JDialog {
    @Serial
    private static final long serialVersionUID = 1L;

    public static void main(final String[] args) {
        final JDialog dialog = new LedDialog(null);
        dialog.setVisible(true);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private DateFormat dateFormatter;
    private JPanel jContentPane;
    private JComponent jLedComponent;
    private transient LedDisplay ledDisplay;
    private DateFormat timeFormatter;
    private Timer timer;

    public LedDialog(final Frame owner) {
        super(owner);

        initialize();
        createTimer();
    }

    private void createTimer() {
        this.timer = new Timer(3000, event -> {
            if (!isShowing()) {
                return;
            }

            final Date currentDate = new Date();

            final String time = getTimeFormatter().format(new Time(currentDate.getTime()));
            final String date = getDateFormatter().format(currentDate);

            final String symbol = time + " " + date;
            final TextDisplayElement displayElement = new TextDisplayElement(symbol);
            getLedDisplay().setDisplayElement(displayElement);

            LedDialog.this.timer.start();
        });

        this.timer.setInitialDelay(5000);
        this.timer.setRepeats(true);
        this.timer.start();
    }

    private DateFormat getDateFormatter() {
        if (this.dateFormatter == null) {
            this.dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        }

        return this.dateFormatter;
    }

    private JPanel getJContentPane() {
        if (this.jContentPane == null) {
            this.jContentPane = new JPanel();
            this.jContentPane.setLayout(new BorderLayout());
            this.jContentPane.add(getJLedComponent(), BorderLayout.CENTER);
        }

        return this.jContentPane;
    }

    private JComponent getJLedComponent() {
        if (this.jLedComponent == null) {
            final String symbol = "Hallo!";
            final TextDisplayElement displayElement = new TextDisplayElement(symbol);
            getLedDisplay().setDisplayElement(displayElement);

            this.jLedComponent = getLedDisplay().getComponent();
        }

        return this.jLedComponent;
    }

    private LedDisplay getLedDisplay() {
        if (this.ledDisplay == null) {
            this.ledDisplay = LedDisplayFactory.createLedDisplay();
            this.ledDisplay.setTokenGap(2);
            this.ledDisplay.setDotSize(2, 2);
            this.ledDisplay.setDotGaps(1, 1);
        }

        return this.ledDisplay;
    }

    private DateFormat getTimeFormatter() {
        if (this.timeFormatter == null) {
            this.timeFormatter = DateFormat.getTimeInstance(DateFormat.LONG, Locale.getDefault());
        }

        return this.timeFormatter;
    }

    private void initialize() {
        setSize(429, 200);
        setContentPane(getJContentPane());
    }
}
