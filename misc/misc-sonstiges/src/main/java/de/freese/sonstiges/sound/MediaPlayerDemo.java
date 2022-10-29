//// Created: 26.04.2012
// package de.freese.sonstiges.sound;
//
// import java.awt.BorderLayout;
// import java.awt.Color;
// import java.awt.Component;
// import java.awt.Container;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.awt.event.WindowAdapter;
// import java.awt.event.WindowEvent;
// import java.io.File;
// import javax.media.ControllerEvent;
// import javax.media.ControllerListener;
// import javax.media.Manager;
// import javax.media.Player;
// import javax.media.RealizeCompleteEvent;
// import javax.swing.JButton;
// import javax.swing.JFileChooser;
// import javax.swing.JFrame;
// import javax.swing.JOptionPane;
//
/// **
// * @author Thomas Freese
// */
// public class MediaPlayerDemo extends JFrame
// {
// /**
// * Inner class to handler events from media player
// *
// * @author Thomas Freese
// */
// private class EventHandler implements ControllerListener
// {
// /**
// * Erstellt ein neues {@link EventHandler} Object.
// */
// public EventHandler()
// {
// super();
// }
//
// /**
// * @see javax.media.ControllerListener#controllerUpdate(javax.media.ControllerEvent)
// */
// @Override
// public void controllerUpdate(final ControllerEvent e)
// {
// if (e instanceof RealizeCompleteEvent)
// {
// Container c = getContentPane();
// c.setBackground(new Color(255, 255, 204));
//
// // load Visual and Control components if they exist
// Component visualComponent = MediaPlayerDemo.this.player.getVisualComponent();
//
// if (visualComponent != null)
// {
// c.add(visualComponent, BorderLayout.CENTER);
// }
//
// c.setBackground(new Color(255, 255, 204));
// Component controlsComponent
// = MediaPlayerDemo.this.player.getControlPanelComponent();
//
// if (controlsComponent != null)
// {
// c.add(controlsComponent, BorderLayout.SOUTH);
// }
//
// c.doLayout();
// }
// }
// }
//
// /**
// *
// */
// private static final long serialVersionUID = 5487817007951030101L;
//
// /**
// * @param args String[]
// */
// public static void main(final String[] args)
// {
// MediaPlayerDemo app = new MediaPlayerDemo();
//
// app.addWindowListener(new WindowAdapter()
// {
// /**
// * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
// */
// @Override
// public void windowClosing(final WindowEvent e)
// {
// System.exit(0);
// }
// });
// }
//
// /**
// *
// */
// private File file;
// /**
// *
// */
// private Player player;
//
// /**
// * Erstellt ein neues {@link MediaPlayerDemo} Object.
// */
// public MediaPlayerDemo()
// {
// super("Demonstrating the Java Media Player");
//
// JButton openFile = new JButton("Open file to play");
// openFile.addActionListener(new ActionListener()
// {
// /**
// * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
// */
// @Override
// public void actionPerformed(final ActionEvent e)
// {
// openFile();
// createPlayer();
// }
// });
//
// getContentPane().add(openFile, BorderLayout.NORTH);
//
// setSize(300, 300);
// setVisible(true);
// }
//
// /**
// *
// */
// private void createPlayer()
// {
// if (this.file == null)
// {
// return;
// }
//
// removePreviousPlayer();
//
// try
// {
// // create a new player and add listener
// this.player = Manager.createPlayer(this.file.toURI().toURL());
// this.player.addControllerListener(new EventHandler());
// this.player.realize();
// this.player.start(); // start player
// }
// catch (Exception e)
// {
// JOptionPane.showMessageDialog(this, "Invalid file or location", "Error loading file",
// JOptionPane.ERROR_MESSAGE);
// }
// }
//
// /**
// *
// */
// private void openFile()
// {
// JFileChooser fileChooser = new JFileChooser();
//
// fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
// int result = fileChooser.showOpenDialog(this);
//
// // user clicked Cancel button on dialog
// if (result == JFileChooser.CANCEL_OPTION)
// {
// this.file = null;
// }
// else
// {
// this.file = fileChooser.getSelectedFile();
// }
// }
//
// /**
// *
// */
// private void removePreviousPlayer()
// {
// if (this.player == null)
// {
// return;
// }
//
// this.player.close();
//
// Component visual = this.player.getVisualComponent();
// Component control = this.player.getControlPanelComponent();
//
// Container c = getContentPane();
//
// if (visual != null)
// {
// c.remove(visual);
// }
//
// if (control != null)
// {
// c.remove(control);
// }
// }
// }
