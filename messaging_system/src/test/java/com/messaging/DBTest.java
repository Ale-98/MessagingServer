package com.messaging;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class DBTest {

	public static void main(String[] args) throws SQLException {
		String url="jdbc:postgresql://localhost/postgres";
		String usr="postgres";
		String pwd="6357";
		
		QueryExecutor qe = new QueryExecutor(url, usr, pwd);
//		qe.removeClientFromDB("Mamma");
		System.out.println(qe.countElements("Msg"));
		System.out.println(qe.getElementsPerPeriod(new Timestamp(6, 8, 2019, 9, 40, 0, 0), new Timestamp(6, 8, 2019, 9, 45, 0, 0), "Msg"));
		List<String> registered = qe.getRegisteredClients();
		for(String s:registered) {
			System.out.println(s);
		}
	}

}
