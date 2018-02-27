package com.stratio.fbi.mafia.model.org.tree;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

public class TreeMafiaOrganization implements MafiaOrganization {

	private MafiaCell cupula;

	private Boolean isDeep;

	@Override
	public void setDeep(Boolean isDeep) {
		this.isDeep = isDeep;
	}

	@Override
	public Boolean isDeep() {
		return isDeep;
	}

	@Override
	public Mafioso getCupula() {
		return cupula.getMafioso();
	}

	@Override
	public void setCupula(Mafioso mafioso) {
		this.cupula = new MafiaCell(mafioso);
	}

	@Override
	public Mafioso getBoss(Mafioso mafioso) {
		return cupula.findMafiaCell(mafioso).getBossCell().getMafioso();
	}

	@Override
	public Iterator<Mafioso> getSubordinates(Mafioso mafioso) {
		return new TreeIterator(new MafiaCellIterator(cupula.findMafiaCell(mafioso), true));
	}

	@Override
	public void addSubordinate(Mafioso boss, Mafioso subordinate) {
		MafiaCell bossCell = cupula.findMafiaCell(boss);
		assert (bossCell != null);
		bossCell.addSubordinate(subordinate);
	}

	@Override
	public void remove(Mafioso subordinate) {
		MafiaCell subordinateCell = cupula.findMafiaCell(subordinate);
		if (subordinateCell == null) {
			return;
		}
		MafiaCell bossCell = subordinateCell.getBossCell();
		List<MafiaCell> subordinates = bossCell.getSubordinates();
		subordinates.remove(subordinateCell);
		MafiaCell oldest = null;
		for (MafiaCell newSubordinate : subordinates) {
			if (oldest == null || oldest.getMafioso().getAge() < newSubordinate.getMafioso().getAge()) {
				oldest = newSubordinate;
			}
		}
		if (oldest != null) {
			bossCell.getSubordinates().add(oldest);
			subordinates.remove(oldest);
			oldest.getSubordinates().addAll(subordinates);
		}
	}

	@Override
	public Iterator<Mafioso> iterator() {
		return new TreeIterator(new MafiaCellIterator(cupula));
	}

	private static class TreeIterator implements Iterator<Mafioso> {

		private MafiaCellIterator iterator;

		public TreeIterator(MafiaCellIterator iterator) {
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Mafioso next() {
			return iterator.next().getMafioso();
		}
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(cupula);
		} catch (JsonProcessingException e) {
			return e.getMessage();
		}
	}

}
