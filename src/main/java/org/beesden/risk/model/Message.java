package org.beesden.risk.model;

import java.util.Date;

public class Message {

	private String action;
	private String object;
	private Date received;
	private String target;

	// Getters and Setters

	public String getAction() {
		return action;
	}

	public String getObject() {
		return object;
	}

	public Date getReceived() {
		return received;
	}

	public String getTarget() {
		return target;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public void setReceived(Date received) {
		this.received = received;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
