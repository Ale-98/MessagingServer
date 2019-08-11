package com.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements MessagingClient{

	private static final long serialVersionUID = 1L;

	private String nick;
	private String password;

	private static BufferedReader br;

	public static void main(String[] args) throws IOException {
		try {
			Registry reg = LocateRegistry.getRegistry();
			MessagingServer server = (MessagingServer)reg.lookup("MessagingServer");
			Client c = new Client();
			boolean state = server.signUp(c, c.getNick(), c.getPassword());
			System.out.println("Utenti loggati: "+server.getLogged());
			System.out.println("Registered: "+state);
			String msg = "";
			String who = "";
			while(!msg.equals("shutdown")) {
				System.out.println("A chi vuoi mandare un messaggio?\n");
				System.out.println("Utenti loggati: "+server.getLogged());
				who = br.readLine();
				System.out.println("Connesso con "+who);
				while(!msg.equalsIgnoreCase("exit")) {
					System.out.println("To: "+who);
					msg = br.readLine();
					server.sendMsg(msg, who); 
				}
			}
		} catch (RemoteException e) {
			System.err.println("Error retiving registry");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println("Client: Server not bounded");
			e.printStackTrace();
		}
	}

	protected Client() throws IOException {
		super();
		br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Inserisci nickname: \n");
		nick = br.readLine();
		System.out.println("Inserisci password: \n");
		password = br.readLine();
		System.out.println("===Benvenuto/a!!===");
	}

	public String getNick() {
		return nick;
	}

	public String getPassword() {
		return password;
	}

	public void receiveMsg(String msg) throws RemoteException {
		System.out.println("Message received: "+msg);
	}

	public void notyNewClient(String nickname) throws RemoteException {
		System.out.println("Nuovo utente iscritto: "+nickname);
	}

	public void notyUnsubscribedClient(String nickname) throws RemoteException {
		System.out.println("Utente disiscritto: "+nickname);
	}

	public void infoMsg(String msg) throws RemoteException {
		System.err.println(msg);
	}



}
