package com.stratio.fbi.mafia.model.org.tree;

import static java.lang.String.format;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

public class TreeMafiaOrganization implements MafiaOrganization {

    private static final Logger LOG = LoggerFactory.getLogger(TreeMafiaOrganization.class);

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
        return new TreeIterator(new MafiaCellIterator(cupula.findMafiaCell(mafioso), false));
	}

	@Override
	public void addSubordinate(Mafioso boss, Mafioso subordinate) {
		MafiaCell bossCell = cupula.findMafiaCell(boss);
		assert (bossCell != null);
		bossCell.addSubordinate(subordinate);
	}

	@Override
    public void removeFromOrganization(Mafioso mafioso) {
        MafiaCell mafiaCell = cupula.findMafiaCell(mafioso);
        if (mafiaCell == null) {
            LOG.warn(format("'%s' is not part of the organization", mafioso));
			return;
		}
        MafiaCell bossCell = mafiaCell.getBossCell();
        List<MafiaCell> siblings = bossCell.getSubordinates();
        siblings.remove(mafiaCell);
        promoteOldestSubordinateOf(bossCell, mafiaCell.getSubordinates());
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

        @Override
        public void remove() {
            iterator.remove();
        }
	}

	@Override
	public String toString() {
		try {
            return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                    .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS).writeValueAsString(cupula);
		} catch (JsonProcessingException e) {
			return e.getMessage();
		}
	}

    private void promoteOldestSubordinateOf(MafiaCell bossCell, List<MafiaCell> siblings) {
        MafiaCell oldest = null;
        for (MafiaCell sibling : siblings) {
            if (oldest == null || oldest.getMafioso().getAge() < sibling.getMafioso().getAge()) {
                oldest = sibling;
            }
        }
        if (oldest != null) {
            bossCell.getSubordinates().add(oldest);
            siblings.remove(oldest);
            oldest.getSubordinates().addAll(siblings);
        }
    }
}
