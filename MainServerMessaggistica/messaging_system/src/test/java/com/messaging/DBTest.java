package com.messaging;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.List;

public class DBTest {

	public static void main(String[] args) throws SQLException, InterruptedException, IOException {
		String url="jdbc:postgresql://localhost/dbMessaging";
		String usr="postgres";
		String pwd="6357";
		
		QueryExecutor qe = QueryExecutor.getInstance(url, usr, pwd);
		
		
//		Timestamp before = new Timestamp(0);
//		Timestamp after = new Timestamp(0);
//		before.setTime(System.currentTimeMillis());
//		Thread.sleep(10);
//		after.setTime(System.currentTimeMillis());
//		System.out.println("Latency: "+(after.getTime()-before.getTime()));
//		System.out.println("Numero messaggi in DB "+qe.countElements("Msg"));
//		System.out.println("=== Clients in DB ===");
//		System.out.println("Current time millis: "+System.nanoTime());
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Insert password");
		String myPass = br.readLine();
		String password = qe.getClientPassword("Ale");
		
		System.out.println((myPass.equals(password)) ? "corretta":"errata");
		System.out.println("La vera password �: "+password);
		
		List<String> registered = qe.getRegisteredClients();
		for(String s:registered) {
			System.out.println(s);
		}
//		Collection<User> users = qe.findAllUsers();
//		System.out.println("Numero users in DB "+users.size());
//		for(User u:users) {
//			System.out.println(u.getNickName()+" "+u.getPassword()+" "+u.getSubDate());
//		}
	}
}
