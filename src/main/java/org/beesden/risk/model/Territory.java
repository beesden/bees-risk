package org.beesden.risk.model;

import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import lombok.Data;
import org.beesden.risk.utils.JsonUtils;

@Data
public class Territory {

	private Integer battalions;
	private Integer cardValue;
	private Integer[] center;
	private Continent continent;
	private String id;
	private String name;
	private HashMap<String, Territory> neighbours = new HashMap<>();
	private Player owner;
	private String path;

	public JsonObject toJson() {
		JsonObjectBuilder object = Json.createObjectBuilder();
		object.add("cardValue", cardValue);
		object.add("center", JsonUtils.toArray(center));
		object.add("continent", continent.getId());
		object.add("id", id);
		object.add("name", name);
		object.add("path", path);
		JsonArrayBuilder neighbourList = Json.createArrayBuilder();
		for (String t : neighbours.keySet()) {
			neighbourList.add(t);
		}
		object.add("neighbours", neighbourList);
		if (battalions != null) {
			object.add("battalions", battalions);
		}
		if (owner != null) {
			object.add("player", owner.getPlayerId());
		}
		return object.build();
	}

}
