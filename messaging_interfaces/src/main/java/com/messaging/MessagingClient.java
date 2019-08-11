package com.messaging;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessagingClient extends Remote{

	public void infoMsg(String msg)throws RemoteException;
	
	public void receiveMsg(String msg)throws RemoteException;
	public void notyNewClient(String nickname)throws RemoteException;
	public void notyUnsubscribedClient(String nickname)throws RemoteException;
	
}
