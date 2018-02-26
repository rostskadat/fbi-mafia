package com.stratio.fbi.mafia.model.org.tree;

import java.util.Iterator;
import java.util.List;

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
        return new TreeIterator(new MafiaCellIterator(cupula.findMafiaCell(mafioso)));
    }

    @Override
    public void addSubordinate(Mafioso boss, Mafioso subordinate) {
        MafiaCell bossCell = cupula.findMafiaCell(boss);
        assert (bossCell != null);
        bossCell.addSubordinate(subordinate);
    }

    @Override
    public void removeSubordinate(Mafioso subordinate) {
        MafiaCell subordinateCell = cupula.findMafiaCell(subordinate);

        MafiaCell bossCell = subordinateCell.getBossCell();
        List<MafiaCell> subordinates = bossCell.getSubordinates();
        subordinates.remove(subordinateCell);
    }

    @Override
    public Iterator<Mafioso> iterator() {
        return new TreeIterator(new MafiaCellIterator(cupula));
    }

    public static class TreeIterator implements Iterator<Mafioso> {

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

}
