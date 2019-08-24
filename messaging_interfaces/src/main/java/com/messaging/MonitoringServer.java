package com.messaging;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;

public interface MonitoringServer extends Remote{

	// API Monitoraggio
	public String getLogged()throws RemoteException;
	public int getRegisteredPerPeriod(LocalDate from, LocalDate to)throws RemoteException;
	public int getMsgsPerPeriod(LocalDate from, LocalDate to)throws RemoteException;
	public double getAvgLatencyPerPeriod(LocalDate from, LocalDate to)throws RemoteException;

}
