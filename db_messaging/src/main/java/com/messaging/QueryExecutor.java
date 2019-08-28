package com.messaging;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class QueryExecutor {

	private Connection con;
	private static QueryExecutor qe;

	// For debbugging and service ----------------------------------------------------------
	public Connection getCon() {
		return con;
	}

	static void printSQLException(SQLException ex){
		System.err.println("SQLState:"+ ex.getSQLState());
		System.err.println("Error code:"+ ex.getErrorCode());
		System.err.println("Message:"+ ex.getMessage());

	}

	private Connection openConnection(String url,String usr, String pwd) throws SQLException{
		Properties props= new Properties();
		props.setProperty("user", usr);
		props.setProperty("password", pwd);

		Connection conn= DriverManager.getConnection(url, props);
		return conn;
	}

	// The builder
	private QueryExecutor(String url, String usr, String pwd) throws SQLException{
		con=openConnection(url,usr,pwd);
		System.out.println("Connesso al db "+con.getCatalog());
		// init DB
		Statement stmt = con.createStatement();
		stmt.executeUpdate(PredefinedSQLCode.create_table_queries[0]);
		stmt.executeUpdate(PredefinedSQLCode.create_table_queries[1]);
	}

	// Singleton pattern
	public static QueryExecutor getInstance(String url, String usr, String pwd) throws SQLException {
		if(qe == null) {
			qe = new QueryExecutor(url, usr, pwd);
		}
		return qe;
	}

	// DDQueryExecutor con parametro... aggiungere parametri e setX(..) corrispondenti per eseguire query parametriche


	// Query methods for messaging system ----------------------------------------------------------
	public synchronized void addClientToDB(String nickname, String password) throws SQLException {
		PreparedStatement stmt = null;
		Timestamp subTime = new Timestamp(0);
		subTime.setTime(System.currentTimeMillis());

		stmt = con.prepareStatement(PredefinedSQLCode.insert_table_queries[0]);
		stmt.setString(1, nickname);
		stmt.setString(2, password);
		stmt.setTimestamp(3, subTime);
		stmt.executeUpdate();
		try {
			if(stmt!=null) stmt.close();
		}catch(SQLException e) {
			System.err.println("Statement not closed");
			e.printStackTrace();
		}
	}

	public synchronized void addMessageToDB(String nickname, String text, String dest, long latency, boolean delivered, String type) throws SQLException {
		PreparedStatement stmt = null;
		Timestamp sendTime = new Timestamp(0);
		sendTime.setTime(System.currentTimeMillis());

		stmt = con.prepareStatement(PredefinedSQLCode.insert_table_queries[1]);
		stmt.setString(1, nickname);
		stmt.setString(2, text);
		stmt.setString(3, dest);
		stmt.setTimestamp(4, sendTime);
		stmt.setLong(5, latency);
		stmt.setBoolean(6, delivered);
		stmt.setString(7, type);
		stmt.executeUpdate();
		try{	
			if(stmt!=null) stmt.close();
		}catch(SQLException e) {
			System.err.println("Statement not closed");
			e.printStackTrace();
		}
	}

	public synchronized List<String> getPendantMessages(String nickname) throws SQLException {
		List<String> pendants = new ArrayList<String>();
		PreparedStatement stmt = null;

		stmt = con.prepareStatement(PredefinedSQLCode.select_queries[6]);
		stmt.setString(1, nickname);
		ResultSet rs = stmt.executeQuery();

		while(rs.next()) {
			int numCol = rs.getMetaData().getColumnCount();
			String row = "";
			for(int i = 1; i<=numCol; i++) {
				String cell = "";
				switch(i) {
				case 1:
				case 2:
				case 3:
					cell = rs.getString(i);
					break;
				case 4:
					cell = cell +""+ rs.getTimestamp(i).toString();
					break;
				}
				row=row+cell+"\t";
			}
			pendants.add(row);
		}
		try {	
			if(stmt!=null) stmt.close();
		}catch(SQLException e) {
			System.err.println("Statement not closed");
			e.printStackTrace();
		}
		return pendants;
	}

	public synchronized void updatePendants(String nickname) throws SQLException {
		PreparedStatement stmt = null;
		stmt = con.prepareStatement(PredefinedSQLCode.update_queries[0]);
		stmt.setString(1,  nickname);
		stmt.executeUpdate();
		try {
			if(stmt!=null) stmt.close();
		}catch(SQLException e) {
			e.printStackTrace();
			printSQLException(e);
		}
	}

	public synchronized void removeClientFromDB(String nickname) throws SQLException {
		PreparedStatement stmt = null;

		stmt = con.prepareStatement(PredefinedSQLCode.delete_queries[0]);
		stmt.setString(1, nickname);
		stmt.executeUpdate();
		try{	
			if(stmt!=null) stmt.close();
		}catch(SQLException e) {
			System.err.println("Statement not closed");
			e.printStackTrace();
		}
	}

	public synchronized List<String> getRegisteredClients() throws SQLException{
		List<String> registered = new ArrayList<String>();
		Statement stmt = null;

		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(PredefinedSQLCode.select_queries[0]);

		while(rs.next()) {
			int numCol = rs.getMetaData().getColumnCount();
			for(int i = 1; i<=numCol; i++) {
				registered.add(rs.getString(i));
			}
		}
		try {	
			if(stmt!=null) stmt.close();
		}catch(SQLException e) {
			System.err.println("Statement not closed");
			e.printStackTrace();
		}
		return registered;
	}

	// For monitoring -------------------------------------------------------------------------
	public synchronized double getAvgLatencyPerPeriod(Date from, Date to) throws SQLException {
		PreparedStatement stmt = null;
		double avg = 0;
		Timestamp before = new Timestamp(0);
		before.setTime(from.getTime());
		Timestamp after = new Timestamp(0);
		after.setTime(to.getTime());

		stmt = con.prepareStatement(PredefinedSQLCode.select_queries[5]);
		stmt.setTimestamp(1, before);
		stmt.setTimestamp(2, after);
		ResultSet rs = stmt.executeQuery();

		while(rs.next()) {
			int numCol = rs.getMetaData().getColumnCount();
			for(int i = 1; i<=numCol; i++) {
				avg = rs.getInt(i);	
			}
		}
		try {	
			if(stmt!=null) stmt.close();
		}catch(SQLException e) {
			System.err.println("Statement not closed");
			e.printStackTrace();
		}
		return avg;
	}

	// Counts elements in DB, if what equals Clients return the number of Clients, if what equals Msg return the number of messages
	public synchronized int countElements(String what) throws SQLException {
		Statement stmt = null;
		int count = 0;

		stmt = con.createStatement();
		ResultSet rs = null;
		if(what.equals("Client")) rs = stmt.executeQuery(PredefinedSQLCode.select_queries[1]);
		else if(what.equals("Msg")) rs = stmt.executeQuery(PredefinedSQLCode.select_queries[2]);
		else System.err.println("Don't know what to count");

		while(rs.next()) {
			int numCol = rs.getMetaData().getColumnCount();
			for(int i = 1; i<=numCol; i++) {
				count = rs.getInt(i);	
			}
		}
		try {	
			if(stmt!=null) stmt.close();
		}catch(SQLException e) {
			System.err.println("Statement not closed");
			e.printStackTrace();
		}
		return count;
	}

	public synchronized int countElementsPerPeriod(Date from, Date to, String what)throws SQLException {
		PreparedStatement stmt = null;
		int count = 0;
		Timestamp before = new Timestamp(0);
		before.setTime(from.getTime());
		Timestamp after = new Timestamp(0);
		after.setTime(to.getTime());

		if(what.equals("Client")) stmt = con.prepareStatement(PredefinedSQLCode.select_queries[3]);
		else if(what.equals("Msg")) stmt = con.prepareStatement(PredefinedSQLCode.select_queries[4]);
		else System.err.println("Don't know what to count");

		stmt.setTimestamp(1, before);
		stmt.setTimestamp(2, after);
		ResultSet rs = stmt.executeQuery();

		while(rs.next()) {
			int numCol = rs.getMetaData().getColumnCount();
			for(int i = 1; i<=numCol; i++) {
				count = rs.getInt(i);	
			}
		}
		try {	
			if(stmt!=null) stmt.close();
		}catch(SQLException e) {
			System.err.println("Statement not closed");
			e.printStackTrace();
		}
		return count;
	}

	// In test per client vaadin ----------------------------------------------------------------
	// For MainView textFiltering by nickname
	public Collection<User> findAllUsers() throws SQLException{

		Collection<User> result = new ArrayList<User>();
		String nickName = null;
		String pwd = null;
		Timestamp sub = null;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(PredefinedSQLCode.select_queries[6]);

			while(rs.next()) {
				int numCol = rs.getMetaData().getColumnCount();
				for(int i = 1; i<=numCol; i++) {
					switch(i) {
					case 1:
						nickName = rs.getString(i);
						break;
					case 2:
						pwd = rs.getString(i);
						break;
					case 3:
						sub = rs.getTimestamp(i);
						break;
					}
				}
				result.add(new User(nickName, pwd, sub));
				//				}
			}
		}
		catch(SQLException e) {	
			e.printStackTrace();
			printSQLException(e);
		}
		finally {	
			if(stmt!=null) stmt.close();
		}
		return result;
	}

	// For MainView textFiltering by nickname
	public Collection<ChatMessage> findAllMsgs() throws SQLException{

		Collection<ChatMessage> result = new ArrayList<ChatMessage>();
		String nickName = null;
		String dest = null;
		Date dataSend = null;
		long latency = 0;
		boolean delivered = false;
		String type = null;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(PredefinedSQLCode.select_queries[7]);

			while(rs.next()) {
				int numCol = rs.getMetaData().getColumnCount();
				for(int i = 1; i<=numCol; i++) {
					switch(i) {
					case 1:
						nickName = rs.getString(i);
						break;
					case 2:
						dest = rs.getString(i);
						break;
					case 3:
						dataSend = rs.getTimestamp(i);
						break;
					case 4:
						latency = rs.getLong(i);
						break;
					case 5:
						delivered = rs.getBoolean(i);
						break;
					case 6:
						type = rs.getString(i);
						break;
					}
				}
				result.add(new ChatMessage(nickName, dest, dataSend, latency, delivered, type));
				//					}
			}
		}
		catch(SQLException e) {	
			e.printStackTrace();
			printSQLException(e);
		}
		finally {	
			if(stmt!=null) stmt.close();
		}
		return result;
	}
}


