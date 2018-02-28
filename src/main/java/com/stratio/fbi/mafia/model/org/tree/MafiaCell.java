package com.stratio.fbi.mafia.model.org.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stratio.fbi.mafia.model.Mafioso;

/**
 * @see https://github.com/gt4dev/yet-another-tree-structure
 */
public class MafiaCell implements Iterable<MafiaCell> {

	private Mafioso mafioso;

    @JsonIgnore
    private MafiaCell bossCell;

	private List<MafiaCell> subordinates;

	private List<MafiaCell> elementsIndex;

	public MafiaCell() {
		this.subordinates = new LinkedList<>();
		this.elementsIndex = new LinkedList<>();
		this.elementsIndex.add(this);
	}

	public MafiaCell(Mafioso mafioso) {
		this.mafioso = mafioso;
		this.subordinates = new LinkedList<>();
		this.elementsIndex = new LinkedList<>();
		this.elementsIndex.add(this);
	}

	public boolean isRoot() {
		return bossCell == null;
	}

	public boolean isLeaf() {
		return subordinates.isEmpty();
	}

	public void addSubordinate(Mafioso subordinate) {
		MafiaCell subordinateNode = new MafiaCell(subordinate);
		subordinateNode.bossCell = this;
		this.subordinates.add(subordinateNode);
		this.registerChildForSearch(subordinateNode);
	}

	public void setBoss(Mafioso boss) {
		this.bossCell = new MafiaCell(boss);
	}

	public int getLevel() {
		if (this.isRoot())
			return 0;
		else
			return bossCell.getLevel() + 1;
	}

	private void registerChildForSearch(MafiaCell node) {
		elementsIndex.add(node);
		if (bossCell != null) {
			bossCell.registerChildForSearch(node);
		}
	}

	public MafiaCell findMafiaCell(Mafioso mafioso) {
		if (mafioso == null) {
			throw new IllegalArgumentException("Mafioso can't be null");
		}
		for (MafiaCell cell : this.elementsIndex) {
			Mafioso other = cell.mafioso;
			if (StringUtils.equals(mafioso.getId(), other.getId())) {
				return cell;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return mafioso != null ? mafioso.toString() : "[mafioso null]";
	}

	public Mafioso getMafioso() {
		return mafioso;
	}

	public void setMafioso(Mafioso mafioso) {
		this.mafioso = mafioso;
	}

	public MafiaCell getBossCell() {
		return bossCell;
	}

	public void setBossCell(MafiaCell bossCell) {
		this.bossCell = bossCell;
	}

	public List<MafiaCell> getSubordinates() {
		return subordinates;
	}

	public void setSubordinates(List<MafiaCell> subordinates) {
		this.subordinates = subordinates;
	}

	@Override
	public Iterator<MafiaCell> iterator() {
		return new MafiaCellIterator(this);
	}

}
