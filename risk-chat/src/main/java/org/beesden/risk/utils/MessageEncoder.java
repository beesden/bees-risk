package org.beesden.risk.utils;

import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.beesden.risk.model.Message;

public class MessageEncoder implements Encoder.Text<Message> {

	@Override
	public void destroy() {
	}

	@Override
	public String encode(final Message command) throws EncodeException {
		return Json.createObjectBuilder()
				.add("response", command.getAction())
				.add("received", command.getReceived().toString())
				.build()
				.toString();
	}

	@Override
	public void init(final EndpointConfig config) {
	}
}
