package com.messaging;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface MessagingServer extends Remote{

	// API Client
	/**
	 * Used by registration form to add a new client into DB, if a client alredy bounded in DB try to signUp, signIn method is executed
	 * @param mc The remote reference of the new registered client
	 * @param nicckname The new registered client's nickname
	 * @param password The new registered client's password
	 * @return true if new client wasn't laready bounded in DB, else false
	 * @throws RemoteException in case of network issues
	 */
	public boolean signUp(MessagingClient mc, String nickaname, String password)throws RemoteException;
	
	/**
	 * Used by logIn form to add a client to the currently logged In clients' list.
	 * @param mc The remote reference to the client to signIn.
	 * @param nickname The new logged client's nickname.
	 * @retun true if the client is bounded into DB, else false.
	 * @throws RemoteException in case of network issues
	 */
	public boolean signIn(MessagingClient mc, String nickname)throws RemoteException;
	
	/**
	 * Send a broadcast or a direct message to all clients given as paramenters
	 * @param from Who has sent the message
	 * @param msg The message to send
	 * @param when The instant in which the message has been sent
	 * @param toClients All the receivers of the Message
	 * @return true if all messages succeded, false if there are messages not sended 
	 * @throws RemoteException in case of network issues
	 */
	public boolean sendMessage(String who, String msg, long when, String...toClients)throws RemoteException;
	
	/**
	 * Delete the subscription of a client.
	 * @param toKill the client to be unsubscribed
	 * @return true if unsubscription succeded
	 * @throws RemoteException in case of network issues
	 */
	public boolean deleteSubscription(String toKill)throws RemoteException;
	
	/**
	 * Logs out a client. The clients reference is removed from server.
	 * @param nickname The logging out client.
	 * @return true if the client is logged before trying to logging out, else false.
	 * @throws RemoteException in case of network issues
	 */
	public boolean logOut(String nickname)throws RemoteException;
	
	/** Returns the list of currently logged clients.
	 * @return the string representing the list of currently logged clients.
	 * @throws RemoteException in case of network issues
	 */
	public Set<String> getLogged()throws RemoteException;
	
}
