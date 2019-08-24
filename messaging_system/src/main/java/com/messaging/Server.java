package com.messaging;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
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

	private boolean sendMsg(String from, String msg, String to, long when, char type) throws RemoteException {
		MessagingClient dest;
		long receiveTime;
		System.out.println("Server got message: "+msg);
		if(logged.containsKey(to)) {
			dest = logged.get(to);
			receiveTime = dest.receiveMsg(msg); // metodo remoto del client
			storeMessage(from, to, true, receiveTime-when, type); // add message to db as delivered = true;
			return true; // messaggio ricevuto
		}
		storeMessage(from, to, false, 0, type);// add message to db as delivered = false;
		return false; // messaggio non ricevuto
	}

	/**
	 * Send a broadcast message to all clients given as paramenters
	 * @param msg The message to send
	 * @param toClients All the receivers o f the Message
	 * @return true if all messages succeded, false if there are messages not sended 
	 */
	public boolean sendMessage(String from, String msg, long when, String... toClients) throws RemoteException {
		List<String> dests = Arrays.asList(toClients);
		char type = dests.size()>1?'b':'d';
		boolean errors = true;
		for(String to:dests) {
			if(!sendMsg(from, msg, to, when, type)) { // server.sendMsg(msg, to);
				errors = false; // almeno un messaggio non è stato ricevuto
			}
		}
		return errors;
	}

	/**
	 * Send a notification to all logged clients notifying the presence of a new subscribed client
	 * @param newClient The new subscriber
	 */
	public void notyNewClient(String newClient) throws RemoteException {
		Set<String> keys = logged.keySet();
		for(String toNotify:keys) {
			logged.get(toNotify).notyNewClient(newClient); // avvisa tutti i client loggati della presenza del nuovo client
			//Aggiungere select query per avvisare client non loggati(forse non lo faccio)
		}
	}

	/**
	 * Logs out a client. The clients reference is removed from server.
	 * @param nickname The logging out client.
	 * @return true if the client is logged before try to logging out, else false.
	 */
	public boolean logOut(String nickname) throws RemoteException {
		if(logged.containsKey(nickname)) {
			logged.remove(nickname);
			return true;
		}
		return false;
	}

	/**
	 * Delete the subscription of a client.
	 * @param toKill the client to be unsubscribed
	 * @return true if unsubscription succeded
	 */
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

	/**
	 * Send a notification to all logged clients to inform that a client has unsubscribed.
	 * @param unsubscribedClient The unsubscribed client.
	 */
	public void notyUnsubscribedClient(String unsubscribedClient) throws RemoteException {
		Set<String> keys = logged.keySet();
		for(String toNotify:keys) {
			logged.get(toNotify).notyUnsubscribedClient(unsubscribedClient); // avvisa tutti i client loggati della presenza del nuovo client
			//Aggiungere select query per avvisare client non loggati
		}
	}

	private boolean storeMessage(String nickName, String dest, boolean delivered, long latency, char type) throws RemoteException {
		try {
			qe.addMessageToDB(nickName, dest, LocalDate.now(), latency, delivered, type);
		} catch (SQLException e) {
			System.err.println("Message not stored on DB");
			e.printStackTrace();
		}
		return false;
	}
	
	/** Return the string representing the list of currently logged clients.
	 * @return the string representing the list of currently logged clients.
	 */
	public String getLogged()throws RemoteException{
		return logged.keySet().toString();
	}

	// For monitoring ------------------------------------------------------------------------
	
	/**
	 * Retrieves from DB the number of clients who got subscribed in the given time interval.
	 * @param from The lower bound for counting clients.
	 * @param to The upper bound for counting clients.
	 * @return The number of subscribed clients in the given time interval.
	 */
	public int getRegisteredPerPeriod(LocalDate from, LocalDate to) throws RemoteException {
		try {
			return qe.countElementsPerPeriod(from, to, "Client");
		} catch (SQLException e) {
			System.err.println("Error getting number of clients from DB in given time interval");
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Retrieves from DB the number of messages sent in the given time interval.
	 * @param from The lower bound for counting messages.
	 * @param to The upper bound for counting messages.
	 * @return The number of messages sent in the given time interval.
	 */
	public int getMsgsPerPeriod(LocalDate from, LocalDate to) throws RemoteException {
		try {
			return qe.countElementsPerPeriod(from, to, "Msg");
		} catch (SQLException e) {
			System.err.println("Error getting number of messages from DB in given time interval");
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Retrieves from DB the avg of latency of sent messages in give time interval.
	 * @param from The lower bound.
	 * @param to The upper bound.
	 * @return The avg of latency of sent messages in give time interval.
	 */
	public double getAvgLatencyPerPeriod(LocalDate from, LocalDate to) throws RemoteException {
		try {
			return qe.getAvgLatencyPerPeriod(from, to);
		} catch (SQLException e) {
			System.out.println("Error getting avg latency of messages from db in given time interval");
			e.printStackTrace();
			return -1;
		}
	}
}
