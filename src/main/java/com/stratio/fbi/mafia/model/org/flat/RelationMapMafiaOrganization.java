package com.stratio.fbi.mafia.model.org.flat;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;
import com.stratio.fbi.mafia.model.org.MafiosoPosition;

/**
 * This class only capture the relation between {@link Mafioso}. Each relation
 * is composed of "bossId,subordinateId". Look at the {@code README.md} for a
 * comparison between the different organization.
 * 
 * @author rostskadat
 *
 */
public class RelationMapMafiaOrganization implements MafiaOrganization {

	private static final String ROOT_KEY = "-";

	protected Map<String, Mafioso> relations = new HashMap<>();

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
        relations = new HashMap<>();
    }

	@Override
	public void setCupula(Mafioso cupula) {
		String path = format("%s/", ROOT_KEY);
		for (Map.Entry<String, Mafioso> entry : relations.entrySet()) {
			if (StringUtils.startsWith(entry.getKey(), path)) {
				relations.remove(entry.getKey());
				break;
			}
		}
		// Ok, let's add it at the head of the list...
		relations.put(format("%s/%s", ROOT_KEY, cupula.getId()), cupula);
	}

	@Override
	public Mafioso getCupula() {
		String path = format("%s/", ROOT_KEY);
		for (Map.Entry<String, Mafioso> entry : relations.entrySet()) {
			if (StringUtils.startsWith(entry.getKey(), path)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public Mafioso getBoss(Mafioso mafioso) {
		String relation = format("/%s", mafioso.getId());
		String bossRelation = null;
		for (Map.Entry<String, Mafioso> entry : relations.entrySet()) {
			if (StringUtils.endsWith(entry.getKey(), relation)) {
				String newPath = entry.getKey();
				bossRelation = "/" + newPath.split("/")[0];
				break;
			}
		}
		for (Map.Entry<String, Mafioso> entry : relations.entrySet()) {
			if (StringUtils.endsWith(entry.getKey(), bossRelation)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public Iterator<Mafioso> getSubordinates(Mafioso mafioso) {
        return new ReadOnlyIterator(getSubordinates(mafioso, isDeepCount()).iterator());
	}

    @Override
    public int getSubordinateCount(Mafioso mafioso) {
        return getSubordinates(mafioso, isDeepCount()).size();
    }

	@Override
	public void addSubordinate(Mafioso boss, Mafioso subordinate) {
		String relation = format("%s/%s", boss.getId(), subordinate.getId());
		String previousRelation = format("/%s", subordinate.getId());
		for (Map.Entry<String, Mafioso> entry : relations.entrySet()) {
			if (StringUtils.endsWith(entry.getKey(), previousRelation)) {
				// I need to make sure that no subordinate has 2 bosses
				relations.remove(entry.getKey());
				break;
			}
		}
		// If not found just set it...
		relations.put(relation, subordinate);
	}

    @Override
    public MafiosoPosition getMafiosoPosition(Mafioso mafioso) {
        Mafioso boss = getBoss(mafioso);
        String bossId = boss != null ? boss.getId() : null;
        final List<String> ids = new ArrayList<>();
        getSubordinates(mafioso, false).forEach(new Consumer<Mafioso>() {
			@Override
			public void accept(Mafioso t) {
				ids.add(t.getId());
			}
        });
        return new MafiosoPosition(bossId, mafioso.getId(), ids);
    }

    @Override
    public void reinstateMafioso(Mafioso boss, Mafioso mafioso, List<Mafioso> directSubordinates) {
        throw new UnsupportedOperationException();
    }

	@Override
    public void removeFromOrganization(Mafioso subordinate) {
		String previousRelation = format("/%s", subordinate.getId());
		for (Map.Entry<String, Mafioso> entry : relations.entrySet()) {
			if (StringUtils.endsWith(entry.getKey(), previousRelation)) {
				String bossId = entry.getKey().split("/")[0];
				relations.remove(entry.getKey());
				promoteOldestSubordinateOf(bossId, subordinate);
				return;
			}
		}
		throw new IllegalArgumentException(format("Mafioso %s is not a subordinate of anyone", subordinate.getId()));
	}

	@Override
	public Iterator<Mafioso> iterator() {
        return new ReadOnlyIterator(relations);
	}

	@Override
	public String toString() {
		try {
            return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(relations);
		} catch (JsonProcessingException e) {
			return e.getMessage();
		}
	}

    private List<Mafioso> getSubordinates(Mafioso mafioso, boolean goDeep) {
        List<Mafioso> subordinates = new ArrayList<>();
        String subordinatePrefix = mafioso.getId() + "/";
        for (Map.Entry<String, Mafioso> entry : relations.entrySet()) {
            if (StringUtils.startsWith(entry.getKey(), subordinatePrefix)) {
                Mafioso subordinate = entry.getValue();
                subordinates.add(subordinate);
                if (goDeep) {
                    subordinates.addAll(asList(getSubordinates(subordinate)));
                }
            }
        }
        return subordinates;
    }

	private List<Mafioso> asList(Iterator<Mafioso> iterator) {
		List<Mafioso> list = new ArrayList<>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

	private void promoteOldestSubordinateOf(String bossId, Mafioso oldBoss) {
		String previousRelation = format("%s/", oldBoss.getId());
		int oldestAge = 0;
		String oldestIndex = "-/";
		List<String> relationsToChange = new ArrayList<>();
		for (Map.Entry<String, Mafioso> entry : relations.entrySet()) {
			if (StringUtils.startsWith(entry.getKey(), previousRelation)) {
				// Ok find out who is the oldest among the subordinate
				if (entry.getValue().getAge() > oldestAge) {
                    oldestAge = entry.getValue().getAge();
					oldestIndex = entry.getKey();
				}
				relationsToChange.add(entry.getKey());
			}
		}
		if (!relationsToChange.isEmpty()) {
			// XXX: Should I use an iterator for speed?
			String promotedBossId = relations.get(oldestIndex).getId();
			for (String relationToChange : relationsToChange) {
				// first remove the old key
				Mafioso mafioso = relations.remove(relationToChange);
				String relation;
				if (StringUtils.equals(relationToChange, oldestIndex)) {
					relation = format("%s/%s", bossId, promotedBossId);
				} else {
					relation = format("%s/%s", promotedBossId, mafioso.getId());
				}
				relations.put(relation, mafioso);
			}
		}
	}

}
