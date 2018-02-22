package com.stratio.fbi.mafia.model;

import java.io.Serializable;
import java.util.List;

public class Mafioso implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String firstName;

	private String lastName;

	private List<String> subordinates;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<String> getSubordinates() {
		return subordinates;
	}

	public void setSubordinates(List<String> subordinates) {
		this.subordinates = subordinates;
	}

}
