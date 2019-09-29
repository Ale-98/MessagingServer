package com.messaging;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface MonitoringServer extends Remote{

	// API Monitoraggio
	
	/** Returns the list of currently logged clients.
	 * @return the string representing the list of currently logged clients.
	 * @throws RemoteException in case of network issues
	 */
	public List<String> getLogged()throws RemoteException;
	
	/** Returns the number of clients currently bounded in DB
	 * @return the number of clients currently bounded in DB
	 */
	public int getRegistered()throws RemoteException;
	
	/**
	 * Returns the number of messages currently bounded in DB
	 * @return the number of messages currently bounded in DB
	 */
	public int getMessages()throws RemoteException;
	
	/**
	 * Retrieves from DB the number of clients who got subscribed in the given time interval.
	 * @param from The lower bound for counting clients.
	 * @param to The upper bound for counting clients.
	 * @return The number of subscribed clients in the given time interval.
	 * @throws RemoteException in case of network issues
	 */
	public int getRegisteredPerPeriod(Date from, Date to)throws RemoteException;
	
	/**
	 * Retrieves from DB the number of messages sent in the given time interval.
	 * @param from The lower bound for counting messages.
	 * @param to The upper bound for counting messages.
	 * @return The number of messages sent in the given time interval.
	 * @throws RemoteException in case of network issues
	 */
	public int getMsgsPerPeriod(Date from, Date to)throws RemoteException;
	
	/**
	 * Retrieves from DB the avg of latency of sent messages in give time interval.
	 * @param from The lower bound.
	 * @param to The upper bound.
	 * @return The avg of latency of sent messages in give time interval.
	 * @throws RemoteException in case of network issues
	 */
	public int getAvgLatencyPerPeriod(Date from, Date to)throws RemoteException;

}
