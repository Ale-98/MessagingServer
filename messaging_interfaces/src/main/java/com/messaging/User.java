package com.messaging;

import java.io.Serializable;

public class User implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;
	
	private String nickName;
	private String password;
	private boolean isAdmin;
	
	public User(String nickName, String pwd) {
		this.nickName = nickName;
		this.password = pwd;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	@Override
	public User clone() throws CloneNotSupportedException {
		return (User) super.clone();
	}
	
}
