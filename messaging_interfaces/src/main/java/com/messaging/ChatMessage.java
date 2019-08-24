package com.messaging;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.sql.Date;

public class ChatMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	private String from;
	private String to;
	private Date dataSend;
	private long latency;
	private boolean delivered;
	private char type;
	
	public ChatMessage(String from, String to, Date datasend, long latency, boolean delivered, char type) {
		this.from = from;
		this.to = to;
		this.dataSend = datasend;
		this.latency = latency;
		this.delivered = delivered;
		this.type = type;
	}
	
	@SuppressWarnings("deprecation")
	private Timestamp toTimestamp(LocalDateTime now) {
		return new Timestamp(now.getYear(), 
							now.getMonthValue(), 
							now.getDayOfMonth(),
							now.getHour(),
							now.getMinute(),
							now.getSecond(),
							now.getNano());
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
}
