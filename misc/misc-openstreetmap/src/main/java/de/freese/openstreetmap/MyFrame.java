package de.freese.openstreetmap;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.freese.openstreetmap.model.OsmModel;

/**
 * @author Thomas Freese
 */
public class MyFrame extends JFrame implements ActionListener {
    @Serial
    private static final long serialVersionUID = 9060490859375473760L;

    private final MyPanel myPanel;

    public MyFrame(final OsmModel model) {
        super("");

        this.myPanel = new MyPanel(model);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        String doWhat = e.getActionCommand();

        if ("ZTF".equals(doWhat)) {
            this.myPanel.zoomToFit();
            // this.myPanel.repaint();
            repaint();
        }
        else if ("+".equals(doWhat)) {
            this.myPanel.zoom(1.5);
            // this.myPanel.repaint();
            repaint();
        }
        else if ("-".equals(doWhat)) {
            this.myPanel.zoom(1 / 1.5);
            // this.myPanel.repaint();
            repaint();
        }
        else if ("N".equals(doWhat)) {
            this.myPanel.scrollVertical(-50);
            // this.myPanel.repaint();
            repaint();
        }
        else if ("W".equals(doWhat)) {
            this.myPanel.scrollHorizontal(-50);
            // this.myPanel.repaint();
            repaint();
        }
        else if ("E".equals(doWhat)) {
            this.myPanel.scrollHorizontal(50);
            // this.myPanel.repaint();
            repaint();
        }
        else if ("S".equals(doWhat)) {
            this.myPanel.scrollVertical(50);
            // this.myPanel.repaint();
            repaint();
        }
    }

    public void initGui() {
        BorderLayout myLayout = new BorderLayout();

        // work on panel
        Panel myButtonPanel = new Panel();
        Button myWorkButton;

        this.myPanel.setLayout(new FlowLayout());
        myWorkButton = new Button("ZTF");
        myWorkButton.addActionListener(this);
        myButtonPanel.add(myWorkButton);
        myWorkButton = new Button("+");
        myWorkButton.addActionListener(this);
        myButtonPanel.add(myWorkButton);

        // inner panel
        Panel navPanel = new Panel();
        GridLayout gridL = new GridLayout();
        gridL.setRows(3);
        gridL.setColumns(3);
        navPanel.setLayout(gridL);
        navPanel.add(new Panel());
        myWorkButton = new Button("N");
        myWorkButton.addActionListener(this);
        navPanel.add(myWorkButton);
        navPanel.add(new Panel());
        myWorkButton = new Button("W");
        myWorkButton.addActionListener(this);
        navPanel.add(myWorkButton);
        navPanel.add(new Panel());
        myWorkButton = new Button("E");
        myWorkButton.addActionListener(this);
        navPanel.add(myWorkButton);
        navPanel.add(new Panel());
        myWorkButton = new Button("S");
        myWorkButton.addActionListener(this);
        navPanel.add(myWorkButton);
        navPanel.add(new Panel());

        // Outer panel again
        myButtonPanel.add(navPanel);
        myWorkButton = new Button("-");
        myWorkButton.addActionListener(this);
        myButtonPanel.add(myWorkButton);

        setLayout(myLayout);
        add(myButtonPanel, BorderLayout.SOUTH);
        add(this.myPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void zoomToFit() {
        this.myPanel.zoomToFit();
    }
}
