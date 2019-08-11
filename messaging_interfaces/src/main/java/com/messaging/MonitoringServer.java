package com.messaging;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;

public interface MonitoringServer extends Remote{

	// API Monitoraggio
	public String getLogged()throws RemoteException;
	public int getRegisteredPerPeriod(Timestamp from, Timestamp to)throws RemoteException;
	public int getMsgsPerPeriod(Timestamp from, Timestamp to)throws RemoteException;
	public double getAvgLatencyPerPeriod(Timestamp from, Timestamp to)throws RemoteException;

}
