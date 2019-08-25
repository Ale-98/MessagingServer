package com.messaging;

import java.util.Date;

public class User{
	
	private String nickName;
	private String password;
	private Date subscription;
	
	public User(String nickName, String pwd, Date subscription) {
		this.nickName = nickName;
		this.password = pwd;
		this.subscription = subscription;
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
	
	public Date getSubDate() {
		return subscription;
	}

	public void setSubDate(Date subscription) {
		this.subscription = subscription;
	}
}
