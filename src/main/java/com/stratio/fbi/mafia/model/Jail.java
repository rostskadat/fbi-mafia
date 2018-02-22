package com.stratio.fbi.mafia.model;

import java.io.Serializable;
import java.util.List;

public class Jail implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Mafioso> prisoners;

	public List<Mafioso> getPrisoners() {
		return prisoners;
	}

	public void setPrisoners(List<Mafioso> prisoners) {
		this.prisoners = prisoners;
	}

}
