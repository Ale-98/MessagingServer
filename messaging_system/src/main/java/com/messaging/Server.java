package com.messaging;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Server extends UnicastRemoteObject implements MessagingServer, MonitoringServer{

	private static final long serialVersionUID = 1L;

	// Connecting to DB
	private QueryExecutor qe;
	String url="jdbc:postgresql://localhost/dbRdF";
	String usr="postgres";
	String pwd="6357";

	// Utenti online (<nickname, riferimento remoto>)
	private HashMap<String, MessagingClient> logged = new HashMap<String, MessagingClient>();

	protected Server() throws RemoteException {
		super();
		try {
			qe = new QueryExecutor(url, usr, pwd);
		} catch (SQLException e) {
			System.err.println("Error connecing to db");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			Registry reg = LocateRegistry.createRegistry(1099);
			Server theServer = new Server();
			reg.rebind("MessagingServer", theServer);
			System.out.println("Server bounded");
		} catch (RemoteException e) {
			System.err.println("Server not bounded");
			e.printStackTrace();
		}
	}

	public boolean signUp(MessagingClient mc, String nickname, String password) throws RemoteException {
		try {
			qe.addClientToDB(nickname, password, false); // Come gestire gli admin?
			notyNewClient(nickname);
		} catch (SQLException e) {
			System.err.println("Error while trying to register to DB");
			mc.infoMsg("Error while trying to register to DB");
			System.err.println("Client already in DB");
			mc.infoMsg("Client already in DB");
			e.printStackTrace();
			signIn(mc, nickname); // Se già presente in DB fa signIn ma ritorna false
			return false;
		} 
		signIn(mc, nickname); // Se non presente in DB si aggiunge a DB e ritorna true
		return true;
	}

	public boolean signIn(MessagingClient mc, String nickname) throws RemoteException {
		List<String> registered = null;
		try {
			registered = qe.getRegisteredClients();
		} catch (SQLException e) {
			System.err.println("Error retriving registered users from DB");
			mc.infoMsg("Error retriving registered users from DB");
			e.printStackTrace();
		}
		// fare select query per vedere se nickname è presente in database
		if(registered.contains(nickname)) {
			logged.put(nickname, mc); // memorizza mc localmente in mappa utenti connessi
			return true;
		}
		else return false;
	}

	public boolean sendMsg(String msg, String to) throws RemoteException {
		MessagingClient dest;
		System.out.println("Server got message: "+msg);
		if(logged.containsKey(to)) {
			dest = logged.get(to);
			dest.receiveMsg(msg); // metodo remoto del client
			// add message to db as delivered = true;
			return true;
		}
		// add message to db as delivered = false;
		return false;
	}

	/**
	 * Send a broadcast message to all clients given as paramenters
	 * @param msg The message to send
	 * @param toClients All the receivers o f the Message
	 * @return true if all messages succeded, false if there are messages not sended 
	 */
	public boolean sendMultiMessage(String msg, String... toClients) throws RemoteException {
		List<String> dests = Arrays.asList(toClients);
		boolean errors = true;
		for(String to:dests) {
			if(!sendMsg(msg, to)) { // server.sendMsg(msg, to);
				errors = false;
			}
		}
		return errors;
	}

	public void notyNewClient(String newClient) throws RemoteException {
		Set<String> keys = logged.keySet();
		for(String toNotify:keys) {
			logged.get(toNotify).notyNewClient(newClient); // avvisa tutti i client loggati della presenza del nuovo client
			//Aggiungere select query per avvisare client non loggati
		}
	}

	public boolean logOut(String nickname) throws RemoteException {
		if(logged.containsKey(nickname)) {
			logged.remove(nickname);
			return true;
		}
		return false;
	}

	public boolean deleteSubscription(String toKill) throws RemoteException {
		try {
			qe.removeClientFromDB(toKill);
		} catch (SQLException e) {
			System.err.println("Error trying to delete subscription of: "+logged.get(toKill));
			logged.get(toKill).infoMsg("Error trying to delete your subscription");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void notyUnsubscribedClient(String unsubscribedClient) throws RemoteException {
		Set<String> keys = logged.keySet();
		for(String toNotify:keys) {
			logged.get(toNotify).notyUnsubscribedClient(unsubscribedClient); // avvisa tutti i client loggati della presenza del nuovo client
			//Aggiungere select query per avvisare client non loggati
		}
	}

	public boolean storeMessage(ChatMessage cm) throws RemoteException {
//		qe.addMessageToDB(nickname, dest, datasend, datareceive, delivered, type);
		return false;
	}
	
	public String getLogged()throws RemoteException{
		return logged.keySet().toString();
	}

	// For monitoring ------------------------------------------------------------------------
	public int getRegisteredPerPeriod(Timestamp from, Timestamp to) throws RemoteException {
		try {
			return qe.getElementsPerPeriod(from, to, "Client");
		} catch (SQLException e) {
			System.err.println("Error getting number of clients from DB in given time interval");
			e.printStackTrace();
			return -1;
		}
	}

	public int getMsgsPerPeriod(Timestamp from, Timestamp to) throws RemoteException {
		try {
			return qe.getElementsPerPeriod(from, to, "Msg");
		} catch (SQLException e) {
			System.err.println("Error getting number of messages from DB in given time interval");
			e.printStackTrace();
			return -1;
		}
	}

	public double getAvgLatencyPerPeriod(Timestamp from, Timestamp to) throws RemoteException {
		try {
			return qe.getAvgLatencyPerPeriod(from, to);
		} catch (SQLException e) {
			System.out.println("Error getting avg latency of messages from db in given time interval");
			e.printStackTrace();
			return -1;
		}
	}
}
