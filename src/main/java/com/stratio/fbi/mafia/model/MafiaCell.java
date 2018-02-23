package com.stratio.fbi.mafia.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @see https://github.com/gt4dev/yet-another-tree-structure
 */
public class MafiaCell implements Iterable<MafiaCell> {

    private Mafioso mafioso;
    private MafiaCell bossCell;
    private List<MafiaCell> subordinates;

    private List<MafiaCell> elementsIndex;

    public boolean isRoot() {
        return bossCell == null;
    }

    public boolean isLeaf() {
        return subordinates.size() == 0;
    }

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

    public MafiaCell addSubordinate(Mafioso subordinate) {
        MafiaCell subordinateNode = new MafiaCell(subordinate);
        subordinateNode.bossCell = this;
        this.subordinates.add(subordinateNode);
        this.registerChildForSearch(subordinateNode);
        return subordinateNode;
    }

    public MafiaCell setBoss(Mafioso boss) {
        this.bossCell = new MafiaCell(boss);
        return this.bossCell;
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

    public MafiaCell findMafiaCell(Comparable<Mafioso> cmp) {
        for (MafiaCell element : this.elementsIndex) {
            Mafioso elData = element.mafioso;
            if (cmp.compareTo(elData) == 0)
                return element;
        }

        return null;
    }

    @Override
    public String toString() {
        return mafioso != null ? mafioso.toString() : "[mafioso null]";
    }

    @Override
    public Iterator<MafiaCell> iterator() {
        return new MafiaCellIterator(this);
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

}
