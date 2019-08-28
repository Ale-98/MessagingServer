package com.messaging;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DebugUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int BUTTON_VERTICAL_SIZE = 30;
	private static final int BUTTON_HORIZONTAL_SIZE = 150;
	private static final int TEXT_AREA_HORIZONTAL_SIZE = 30;
	private static final int TEXT_AREA_VERTICAL_SIZE = 20;
	
	private JLabel debug;
	private JTextArea debugWindow;
	private JButton stopServer;

	public void showInDebugWindow(String text) {
		debugWindow.setText(debugWindow.getText()+text+"\n");
	}
	
	public DebugUI() {
		super("DebugUI");

		Container con = getContentPane();
		con.setLayout(new FlowLayout());

		debug = new JLabel("Debug Window");
		debug.setAlignmentX(LEFT_ALIGNMENT);
		con.add(debug);

		debugWindow = new JTextArea("", TEXT_AREA_VERTICAL_SIZE, TEXT_AREA_HORIZONTAL_SIZE);
		debugWindow.setEditable(false);
		con.add(new JScrollPane(debugWindow));

		Dimension buttonDimension = new Dimension(BUTTON_HORIZONTAL_SIZE, BUTTON_VERTICAL_SIZE);
		stopServer = new JButton("Stop Server");
		stopServer.addActionListener(new StopButtonHandler());
		stopServer.setPreferredSize(buttonDimension);
		con.add(stopServer);

		setVisible(true);
		setSize(30*11, 30*15);
	}
	
	private class StopButtonHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {
//			theServer.stopServer();
			debugWindow.setText("Stop btn pressed");
		}

	}
	
}
