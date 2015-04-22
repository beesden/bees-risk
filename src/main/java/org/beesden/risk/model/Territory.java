package org.beesden.risk.model;

import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.beesden.risk.utils.JsonUtils;

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

	public Integer getBattalions() {
		return battalions;
	}

	public Integer getCardValue() {
		return cardValue;
	}

	public Integer[] getCenter() {
		return center;
	}

	public Continent getContinent() {
		return continent;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public HashMap<String, Territory> getNeighbours() {
		return neighbours;
	}

	public Player getOwner() {
		return owner;
	}

	public String getPath() {
		return path;
	}

	public void setBattalions(Integer battalions) {
		this.battalions = battalions;
	}

	public void setCardValue(Integer cardValue) {
		this.cardValue = cardValue;
	}

	public void setCenter(Integer[] center) {
		this.center = center;
	}

	public void setContinent(Continent continent) {
		this.continent = continent;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNeighbours(HashMap<String, Territory> neighbours) {
		this.neighbours = neighbours;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public void setPath(String path) {
		this.path = path;
	}

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
