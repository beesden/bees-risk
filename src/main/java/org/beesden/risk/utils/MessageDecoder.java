package org.beesden.risk.utils;

import java.io.StringReader;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.beesden.risk.model.Message;

public class MessageDecoder implements Decoder.Text<Message> {

	@Override
	public Message decode(final String textMessage) throws DecodeException {
		JsonObject obj = Json.createReader(new StringReader(textMessage)).readObject();
		Message command = new Message();
		// Action is mandatory
		command.setAction(obj.getString("action"));
		// Object and target and optional
		// if (obj.getString("object") != null) {
		// command.setObject(obj.getString("object"));
		// }
		// if (obj.getString("target") != null) {
		// command.setObject(obj.getString("target"));
		// }
		command.setReceived(new Date());
		return command;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(final EndpointConfig config) {
	}

	@Override
	public boolean willDecode(final String s) {
		return true;
	}
}
