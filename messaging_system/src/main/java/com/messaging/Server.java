package com.messaging;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class Server extends UnicastRemoteObject implements MessagingServer, MonitoringServer{

	private static final long serialVersionUID = 1L;

	// Connecting to DB
	private QueryExecutor qe;
	
	// Notificate GUI
	private DebugUI debugUI;

	// Utenti online (<nickname, riferimento remoto>)
	private HashMap<String, MessagingClient> logged = new HashMap<String, MessagingClient>();

	// Starts the Server
	protected Server(DebugUI debugUI, String url, String usr, char[] cs) throws RemoteException {
		super();
		this.debugUI = debugUI;
		try {
			Registry reg = LocateRegistry.createRegistry(1099);
			connectToDB(url, usr, cs);
			reg.rebind("MessagingServer", this);
			debugUI.showInDebugWindow("Server bounded");
		} catch (RemoteException e) {
			debugUI.showInDebugWindow("Server not bounded");
			e.printStackTrace();
		}
	}
	
	// Connects to DB
	private void connectToDB(String url, String usr, char[] cs) {
		String pwd = "";
		for(int i = 0; i<cs.length; i++) {
			pwd+=cs[i];
		}
		try {
			qe = QueryExecutor.getInstance(url, usr, pwd);
		} catch (SQLException e) {
			debugUI.showInDebugWindow("Error connecting to db");
			debugUI.showErrorNotification();
			e.printStackTrace();
		}
	}

	// Unexports server remote object
	public void stopServer() {
		try {
			UnicastRemoteObject.unexportObject(this, true);
		} catch (NoSuchObjectException e) {
			debugUI.showInDebugWindow("Error: Server not stopped");
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		try {
//			Registry reg = LocateRegistry.createRegistry(1099);
//			Server theServer = new Server();
//			reg.rebind("MessagingServer", theServer);
//			System.out.println("Server bounded");
//		} catch (RemoteException e) {
//			System.err.println("Server not bounded");
//			e.printStackTrace();
//		}
//	}

	// Services for clients ---------------------------------------------------------------------------
	/**
	 * Used by registration form to add a new client into DB, if a client alredy bounded in DB try to signUp, signIn method is executed
	 * @param mc The remote reference of the new registered client
	 * @param nicckname The new registered client's nickname
	 * @param password The new registered client's password
	 * @return true if new client wasn't laready bounded in DB, else false
	 * @throws RemoteException in case of network issues
	 */
	public boolean signUp(MessagingClient mc, String nickname, String password) throws RemoteException {
		try {
			qe.addClientToDB(nickname, password); // Come gestire gli admin?
			notyNewClient(nickname);
		} catch (SQLException e) {
			debugUI.showInDebugWindow("Error while trying to register to DB");
			mc.infoMsg("Error while trying to register to DB");
			debugUI.showInDebugWindow("Client "+nickname+" already in DB");
			mc.infoMsg("Client "+nickname+" already in DB");
			e.printStackTrace();
			signIn(mc, nickname); // Se già presente in DB fa signIn ma ritorna false
			debugUI.showInDebugWindow("Client "+nickname+" signed-In");
			mc.infoMsg("Client "+nickname+" signed-In");
			return false;
		} 
		signIn(mc, nickname); // Se non presente in DB si aggiunge a DB e ritorna true
		return true;
	}

	/**
	 * Used by logIn form to add a client to the currently logged In clients' list.
	 * @param mc The remote reference to the client to signIn.
	 * @param nickname The new logged client's nickname.
	 * @retun true if the client is bounded into DB, else false.
	 * @throws RemoteException in case of network issues
	 */
	public boolean signIn(MessagingClient mc, String nickname) throws RemoteException {
		List<String> registered = null;
		try {
			registered = qe.getRegisteredClients();
		} catch (SQLException e) {
			debugUI.showInDebugWindow("Error retriving registered users from DB");
			mc.infoMsg("Error retriving registered users from DB");
			e.printStackTrace();
		}
		if(registered.contains(nickname)) {
			logged.put(nickname, mc); // memorizza mc localmente in mappa utenti connessi
			sendPendantMessagesIfThereAre(mc, nickname);
			return true;
		}
		else return false;
	}

	// Retrieves from the DB all messages that hasn't been sent yet and send them to the right client
	private void sendPendantMessagesIfThereAre(MessagingClient mc, String nickname) throws RemoteException {
		try {
			List<String> pendants = qe.getPendantMessages(nickname);
			if(pendants.size()>0) {
				StringTokenizer stk;
				String from, text, to, datasend;
				for(String msg:pendants) {
					stk = new StringTokenizer(msg, "\t");
					from = stk.nextToken();
					text = stk.nextToken();
					to = stk.nextToken();
					datasend = stk.nextToken();
					mc.receiveMsg(text);
				}
				try {
					qe.updatePendants(nickname);
				}catch(SQLException ex) {
					debugUI.showInDebugWindow("Error updating pendant messages state");
					ex.printStackTrace();
				}
			}
		} catch (SQLException e) {
			debugUI.showInDebugWindow("Error retrieving pendant messages from DB");
			e.printStackTrace();
		}
	}

	// Send a single message and store it into DB
	private boolean sendMsg(String from, String msg, String to, long when, String type) throws RemoteException {
		MessagingClient dest;
		long receiveTime;
		debugUI.showInDebugWindow("Server got message from "+from+": "+msg);
		if(logged.containsKey(to)) {
			dest = logged.get(to);
			receiveTime = dest.receiveMsg(msg); // metodo remoto del client
			storeMessage(from, msg, to, receiveTime-when, true, type); // add message to db as delivered = true;
			return true; // messaggio ricevuto
		}
		storeMessage(from, msg, to, 0, false, type);// add message to db as delivered = false;
		return false; // messaggio non ricevuto
	}

	/**
	 * Send a broadcast or a direct message to all clients given as paramenters
	 * @param from Who has sent the message(nickname)
	 * @param msg The message to send
	 * @param when The instant in which the message has been sent
	 * @param toClients All the receivers of the Message
	 * @return true if all messages succeded, false if there are messages not sended 
	 * @throws RemoteException in case of network issues
	 */
	public boolean sendMessage(String from, String msg, long when, String... toClients) throws RemoteException {
		List<String> dests = Arrays.asList(toClients);
		String type = dests.size()>1?"broadcast":"direct";
		boolean errors = true;
		for(String to:dests) {
			if(!sendMsg(from, msg, to, when, type)) { 
				errors = false; // almeno un messaggio non è stato ricevuto
			}
		}
		return errors;
	}

	/**
	 * Send a notification to all logged clients notifying the presence of a new subscribed client
	 * @param newClient The new subscriber
	 * @throws RemoteException in case of network issues
	 */
	private void notyNewClient(String newClient) throws RemoteException {
		Set<String> keys = logged.keySet();
		for(String toNotify:keys) {
			logged.get(toNotify).notyNewClient(newClient); // avvisa tutti i client loggati della presenza del nuovo client
		}
	}

	/**
	 * Logs out a client. The clients reference is removed from server.
	 * @param nickname The logging out client.
	 * @return true if the client is logged before trying to logging out, else false.
	 * @throws RemoteException in case of network issues
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
	 * @throws RemoteException in case of network issues
	 */
	public boolean deleteSubscription(String toKill) throws RemoteException {
		try {
			qe.removeClientFromDB(toKill);
			notyUnsubscribedClient(toKill);
		} catch (SQLException e) {
			debugUI.showInDebugWindow("Error trying to delete subscription of: "+logged.get(toKill));
			logged.get(toKill).infoMsg("Error trying to delete your subscription");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Send a notification to all logged clients to inform that a client has unsubscribed.
	 * @param unsubscribedClient The unsubscribed client.
	 * @throws RemoteException in case of network issues
	 */
	private void notyUnsubscribedClient(String unsubscribedClient) throws RemoteException {
		Set<String> keys = logged.keySet();
		for(String toNotify:keys) {
			logged.get(toNotify).notyUnsubscribedClient(unsubscribedClient); // avvisa tutti i client loggati della presenza del nuovo client
		}
	}

	// Store a message into DB
	private boolean storeMessage(String nickName, String text, String dest,  long latency, boolean delivered, String type) throws RemoteException {
		try {
			qe.addMessageToDB(nickName, text, dest, latency, delivered, type);
		} catch (SQLException e) {
			debugUI.showInDebugWindow("Message not stored on DB");
			e.printStackTrace();
		}
		return false;
	}

	/** Returns the list of currently logged clients.
	 * @return the string representing the list of currently logged clients.
	 * @throws RemoteException in case of network issues
	 */
	public List<String> getLogged()throws RemoteException{
		return new ArrayList<String>(logged.keySet());
	}

	// For monitoring ------------------------------------------------------------------------

	/** Returns the number of clients currently bounded in DB
	 * @return the number of clients currently bounded in DB
	 */
	public int getRegistered() throws RemoteException {
		try {
			return qe.countElements("Client");
		} catch (SQLException e) {
			debugUI.showInDebugWindow("Error getting current number of clients from DB");
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Returns the number of messages currently bounded in DB
	 * @return the number of messages currently bounded in DB
	 */
	public int getMessages() throws RemoteException {
		try {
			return qe.countElements("Msg");
		} catch (SQLException e) {
			debugUI.showInDebugWindow("Error getting current number of clients from DB");
			e.printStackTrace();
			return -1;
		}
	}
	/**
	 * Retrieves from DB the number of clients who got subscribed in the given time interval.
	 * @param from The lower bound for counting clients.
	 * @param to The upper bound for counting clients.
	 * @return The number of subscribed clients in the given time interval.
	 * @throws RemoteException in case of network issues
	 */
	public int getRegisteredPerPeriod(Date from, Date to) throws RemoteException {
		try {
			return qe.countElementsPerPeriod(from, to, "Client");
		} catch (SQLException e) {
			debugUI.showInDebugWindow("Error getting number of clients from DB in given time interval");
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Retrieves from DB the number of messages sent in the given time interval.
	 * @param from The lower bound for counting messages.
	 * @param to The upper bound for counting messages.
	 * @return The number of messages sent in the given time interval.
	 * @throws RemoteException in case of network issues
	 */
	public int getMsgsPerPeriod(Date from, Date to) throws RemoteException {
		try {
			return qe.countElementsPerPeriod(from, to, "Msg");
		} catch (SQLException e) {
			debugUI.showInDebugWindow("Error getting number of messages from DB in given time interval");
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Retrieves from DB the avg of latency of sent messages in give time interval.
	 * @param from The lower bound.
	 * @param to The upper bound.
	 * @return The avg of latency of sent messages in give time interval.
	 * @throws RemoteException in case of network issues
	 */
	public double getAvgLatencyPerPeriod(Date from, Date to) throws RemoteException {
		try {
			return qe.getAvgLatencyPerPeriod(from, to);
		} catch (SQLException e) {
			debugUI.showInDebugWindow("Error getting avg latency of messages from db in given time interval");
			e.printStackTrace();
			return -1;
		}
	}
}
