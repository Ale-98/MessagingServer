package com.messaging;

import java.io.Serializable;

public class ChatMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	private String text;
	private String from;
	private String to;
	private long timeSend;
	
	public ChatMessage(String text, String from, String to) {
		this.text = text;
		this.from = from;
		this.to = to;
		timeSend = System.currentTimeMillis(); // For measuring latency
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getTimeSend() {
		return timeSend;
	}

	public void setTimeSend(long timeSend) {
		this.timeSend = timeSend;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}
	
}
