package com.messaging;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	private String from;
	private String to;
	private Date dataSend;
	private long latency;
	private boolean delivered;
	private String type;
	
	public ChatMessage(String from, String to, Date datasend, long latency, boolean delivered, String type) {
		this.from = from;
		this.to = to;
		this.dataSend = datasend;
		this.latency = latency;
		this.delivered = delivered;
		this.type = type;
	}
	
	public Date getDataSend() {
		return dataSend;
	}

	public void setDataSend(Date dataSend) {
		this.dataSend = dataSend;
	}

	public float getLatency() {
		return latency;
	}

	public void setLatency(long latency) {
		this.latency = latency;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
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
}
