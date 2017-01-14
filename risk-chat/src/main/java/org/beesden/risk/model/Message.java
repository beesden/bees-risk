package org.beesden.risk.model;

import lombok.Data;

import java.util.Date;

@Data
public class Message {

	private String action;
	private String object;
	private Date received;
	private String target;

}
