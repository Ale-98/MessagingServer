package com.messaging;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface MonitoringServer extends Remote{

	// API Monitoraggio
	public String getLogged()throws RemoteException;
	public int getRegisteredPerPeriod(Date from, Date to)throws RemoteException;
	public int getMsgsPerPeriod(Date from, Date to)throws RemoteException;
	public double getAvgLatencyPerPeriod(Date from, Date to)throws RemoteException;

}
