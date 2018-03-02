package com.stratio.fbi.mafia.model.org.tree;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;
import com.stratio.fbi.mafia.model.org.MafiosoPosition;

public class TreeMafiaOrganization implements MafiaOrganization {

	private MafiaCell cupula;

	private Boolean isDeep;

	@Override
	public void setDeepCount(Boolean isDeep) {
		this.isDeep = isDeep;
	}

	@Override
	public Boolean isDeepCount() {
		return isDeep;
	}

    @Override
    public void erase() {
        cupula = new MafiaCell();
    }

    public MafiaCell getCupulaForSerialization() {
        return cupula;
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
        MafiaCell bossCell = cupula.findMafiaCell(mafioso).getBossCell();
        if (bossCell == null) {
            // It's the boss!
            return null;
        }
        return bossCell.getMafioso();
	}

	@Override
	public Iterator<Mafioso> getSubordinates(Mafioso mafioso) {
        return getSubordinates(mafioso, true);
	}

    @Override
    public int getSubordinateCount(Mafioso mafioso) {
        Iterator<Mafioso> i = getSubordinates(mafioso, true);
        int count = 0;
        while (i.hasNext()) {
            i.next(); count++;
        }
        return count;
    }

	@Override
	public void addSubordinate(Mafioso boss, Mafioso subordinate) {
		MafiaCell bossCell = cupula.findMafiaCell(boss);
		assert (bossCell != null);
		bossCell.addSubordinate(subordinate);
	}

	@Override
    public MafiosoPosition getMafiosoPosition(Mafioso mafioso) {
        Mafioso boss = getBoss(mafioso);
        String bossId = boss != null ? boss.getId() : null;
        List<String> subordinateIds = new ArrayList<>();
        Iterator<Mafioso> i = getSubordinates(mafioso, false);
        while (i.hasNext()) {
        		subordinateIds.add(i.next().getId());
        }
        return new MafiosoPosition(bossId, mafioso.getId(), subordinateIds);
    }

    @Override
    public void reinstateMafioso(Mafioso boss, Mafioso mafioso, List<Mafioso> directSubordinates) {
        if (boss != null) {
            MafiaCell oldBossCell = cupula.findMafiaCell(boss);
            MafiaCell newBossCell = new MafiaCell(mafioso);
            newBossCell.addSubordinate(oldBossCell.getMafioso());
            oldBossCell.setBossCell(oldBossCell);
        } else {
            // I'm reinstating the Cupula. In this case I need to make sure that the MafiaCell is properly configured
            MafiaCell oldCupula = cupula;
            MafiaCell newCupula = new MafiaCell(mafioso);
            newCupula.setSubordinates(oldCupula.getSubordinates());
            newCupula.addSubordinate(oldCupula.getMafioso());
            oldCupula.setBossCell(newCupula);
            cupula = newCupula;
        }
    }

    @Override
    public void removeFromOrganization(Mafioso mafioso) {
        MafiaCell mafiaCell = cupula.findMafiaCell(mafioso);
        if (mafiaCell == null) {
            throw new IllegalArgumentException(format("Mafioso #%s is not part of the organization", mafioso.getId()));
		}
        MafiaCell bossCell = mafiaCell.getBossCell();
        if (bossCell == null) {
            cupula = promoteOldestOf(mafiaCell.getSubordinates());
        } else {
            List<MafiaCell> siblings = bossCell.getSubordinates();
            siblings.remove(mafiaCell);
            promoteOldestSubordinateOf(bossCell, mafiaCell.getSubordinates());
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

    private Iterator<Mafioso> getSubordinates(Mafioso mafioso, boolean goDeep) {
        MafiaCell mafiaCell = cupula.findMafiaCell(mafioso);
        if (mafiaCell == null) {
            throw new IllegalArgumentException(format("Mafios %s does not belong to the organization", mafioso));
        }
        return new TreeIterator(new MafiaCellIterator(mafiaCell, false, goDeep ? -1 : mafiaCell.getLevel() + 1));
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

    private MafiaCell promoteOldestOf(List<MafiaCell> siblings) {
        MafiaCell oldest = null;
        for (MafiaCell sibling : siblings) {
            if (oldest == null || oldest.getMafioso().getAge() < sibling.getMafioso().getAge()) {
                oldest = sibling;
            }
        }
        if (oldest != null) {
            siblings.remove(oldest);
            oldest.getSubordinates().addAll(siblings);
            oldest.setBossCell(null);
        }
        return oldest;
    }

}
