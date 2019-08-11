package com.messaging;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.StringTokenizer;

public class DBConsole extends JFrame{

	private static final long serialVersionUID = 1L;

	private static final int BUTTON_VERTICAL_SIZE = 30;
	private static final int BUTTON_HORIZONTAL_SIZE = 150;
	private static final int TEXT_AREA_HORIZONTAL_SIZE = 30;
	private static final int TEXT_AREA_VERTICAL_SIZE = 10;

	private JTextArea input, output;
	private JButton start, clear;
	private JLabel inputIndex, outputIndex;

	// Logging into DB
	String url="jdbc:postgresql://localhost/postgres";
	String usr="postgres";
	String pwd="6357";

	public DBConsole() {
		super("Query_Executor");

		Container contenitore = getContentPane();
		contenitore.setLayout(new FlowLayout());

		inputIndex = new JLabel("Input query:");
		inputIndex.setAlignmentX(LEFT_ALIGNMENT);
		contenitore.add(inputIndex);

		input = new JTextArea("", TEXT_AREA_VERTICAL_SIZE, TEXT_AREA_HORIZONTAL_SIZE);
		contenitore.add(new JScrollPane(input));
		
		outputIndex = new JLabel("Output:");
		outputIndex.setAlignmentX(RIGHT_ALIGNMENT);
		contenitore.add(outputIndex);

		output = new JTextArea("", TEXT_AREA_VERTICAL_SIZE, TEXT_AREA_HORIZONTAL_SIZE);
		output.setEditable(false);
		contenitore.add(new JScrollPane(output));

		Dimension buttonDimension = new Dimension(BUTTON_HORIZONTAL_SIZE, BUTTON_VERTICAL_SIZE);
		queryExecutionHandler execution = new queryExecutionHandler(); 

		start = new JButton("Execute");
		start.setPreferredSize(buttonDimension);
		start.addActionListener(execution);
		contenitore.add(start);

		clear = new JButton("Clear");
		clear.setPreferredSize(buttonDimension);
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setText("");
				output.setText("");
			}
		});
		contenitore.add(clear);
		
		//Scrollbar bar = new Scrollbar();
		//contenitore.add(bar);
		
		setVisible(true);
		setSize(TEXT_AREA_HORIZONTAL_SIZE*11, TEXT_AREA_VERTICAL_SIZE*46);
	}

	// Handling
	private class queryExecutionHandler implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String query = input.getText();
			output.setText(chooseAndExecuteQuery(query));
			
		}
	}

	private String chooseAndExecuteQuery(String query) {

		String result = "";
		try {
			QueryExecutor qe = new QueryExecutor(url, usr, pwd);
			StringTokenizer stk = new StringTokenizer(query, " ");
			String type = stk.nextToken();
			if(type.equals("create")||type.equals("drop")) {
				qe.execute_DD_query(query);
			}
			else if(type.equals("select")) {
				result = qe.execute_SEL_query(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			QueryExecutor.printSQLException(e);
		}
		return result;
	}
	
public static void main(String[] args) {
		
		DBConsole application = new DBConsole();
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
}
