package com.messaging;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	private String text;
	private long timeSend;
	
	private String from;
	private String to;
	private Date dataSend;
	private Date dataReceive;
	private float latency;
	private boolean delivered;
	private char type;
	
	public ChatMessage(String text, String from, String to) {
		this.text = text;
		this.from = from;
		this.to = to;
		timeSend = System.currentTimeMillis(); // For measuring latency
	}
	
	public Date getDataSend() {
		return dataSend;
	}

	public void setDataSend(Date dataSend) {
		this.dataSend = dataSend;
	}

	public Date getDataReceive() {
		return dataReceive;
	}

	public void setDataReceive(Date dataReceive) {
		this.dataReceive = dataReceive;
	}

	public float getLatency() {
		return latency;
	}

	public void setLatency(float latency) {
		this.latency = latency;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
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
	
}
