package com.messaging;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class DBTest {

	public static void main(String[] args) throws SQLException {
		String url="jdbc:postgresql://localhost/dbRdF";
		String usr="postgres";
		String pwd="6357";
		
		QueryExecutor qe = QueryExecutor.getInstance(url, usr, pwd);
//		qe.removeClientFromDB("Mamma");
		System.out.println("Numero messaggi in DB "+qe.countElements("Msg"));
//		System.out.println(qe.getElementsPerPeriod(new Timestamp(6, 8, 2019, 9, 40, 0, 0), new Timestamp(6, 8, 2019, 9, 45, 0, 0), "Msg"));
		System.out.println("=== Clients in DB ===");
		List<String> registered = qe.getRegisteredClients();
		for(String s:registered) {
			System.out.println(s);
		}
		Collection<User> users = qe.findAll();
		System.out.println("Numero users in DB "+users.size());
		for(User u:users) {
			System.out.println(u.getNickName()+" "+u.getPassword()+" "+u.isAdmin());
		}
	}

}
