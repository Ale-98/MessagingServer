package com.messaging;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ServerUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private static Server theServer;

	private static final int BUTTON_VERTICAL_SIZE = 30;
	private static final int BUTTON_HORIZONTAL_SIZE = 150;
	private static final int TEXT_FIELD_HORIZONTAL_SIZE = 300;
	private static final int TEXT_FIELD_VERTICAL_SIZE = 30;

	private JLabel title, URL, user, pwd;
	private JTextField urlField, userField;
	private JPasswordField pwdField;
	private JButton start;

	public static Server getServer() {
		return theServer;
	}
	
	public ServerUI() {
		super("ServerUI");

		Container con = getContentPane();
		con.setLayout(new FlowLayout());

		title = new JLabel("Insert DB coordinates to start Server");
		title.setAlignmentX(LEFT_ALIGNMENT);
		con.add(title);

		JLabel separator = new JLabel("______________________________________________");
		con.add(separator);

		URL = new JLabel("URL");
		URL.setAlignmentX(LEFT_ALIGNMENT);
		con.add(URL);

		Dimension textFieldDimension = new Dimension(TEXT_FIELD_HORIZONTAL_SIZE, TEXT_FIELD_VERTICAL_SIZE);
		urlField = new JTextField("jdbc:postgresql://localhost/dbMessaging");
		urlField.setPreferredSize(textFieldDimension);
		urlField.setAlignmentX(LEFT_ALIGNMENT);
		con.add(urlField);

		user = new JLabel("User");
		user.setAlignmentX(LEFT_ALIGNMENT);
		con.add(user);

		userField = new JTextField();
		userField.setPreferredSize(textFieldDimension);
		userField.setAlignmentX(LEFT_ALIGNMENT);
		con.add(userField);

		pwd = new JLabel("Password");
		pwd.setAlignmentX(LEFT_ALIGNMENT);
		con.add(pwd);

		pwdField = new JPasswordField();
		pwdField.setPreferredSize(textFieldDimension);
		pwdField.setAlignmentX(LEFT_ALIGNMENT);
		con.add(pwdField);

		Dimension buttonDimension = new Dimension(BUTTON_HORIZONTAL_SIZE, BUTTON_VERTICAL_SIZE);
		start = new JButton("Start");
		start.setPreferredSize(buttonDimension);
		start.addActionListener(new StartButtonHandler());
		start.setAlignmentX(LEFT_ALIGNMENT);
		con.add(start);

		setVisible(true);
		setSize(30*11, 30*10);
	}

	// Handling------------------------------------------------

	private class StartButtonHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			try {
				DebugUI debug= new DebugUI();
				theServer = new Server(debug, urlField.getText(), userField.getText(), pwdField.getPassword());
			} catch (RemoteException e1) {
				System.err.println("Server not bounded");
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ServerUI application = new ServerUI();
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
