package com.messaging;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessagingServer extends Remote{

	// API Client
	public boolean signUp(MessagingClient mc, String nickaname, String password)throws RemoteException;
	public boolean signIn(MessagingClient mc, String nickname)throws RemoteException;
	public boolean sendMsg(String msg, String to)throws RemoteException;
	public boolean sendMultiMessage(String msg, String...toClients)throws RemoteException;
	public void notyNewClient(String newClient)throws RemoteException;
	public boolean deleteSubscription(String toKill)throws RemoteException;
	public void notyUnsubscribedClient(String unsubscribedClient)throws RemoteException;
	public boolean logOut(String nickname)throws RemoteException;
	public String getLogged()throws RemoteException;
	
}
