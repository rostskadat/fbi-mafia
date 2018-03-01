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
 * This class capture the whole hierarchy for each {@link Mafioso}. The key is
 * composed of <bossId1[, bossIdN], subordinateId>
 * 
 * @author rostskadat
 *
 */
public class PathMapMafiaOrganization implements MafiaOrganization {

	private static final String ROOT_KEY = "-";

	protected Map<String, Mafioso> paths = new HashMap<>();

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
        paths = new HashMap<>();
    }

	@Override
	public void setCupula(Mafioso cupula) {
        Map.Entry<String, Mafioso> entry = getCupulaEntry();
        if (entry != null) {
            paths.remove(entry.getKey());
        }
		// Ok, let's add it at the head of the list...
		paths.put(format("%s/%s", ROOT_KEY, cupula.getId()), cupula);
	}

	@Override
	public Mafioso getCupula() {
        Map.Entry<String, Mafioso> entry = getCupulaEntry();
        if (entry != null) {
				return entry.getValue();
		}
		return null;
	}

	@Override
	public Mafioso getBoss(Mafioso mafioso) {
		String pathSuffix = format("/%s", mafioso.getId());
		String bossPath = null;
		for (Map.Entry<String, Mafioso> entry : paths.entrySet()) {
			if (StringUtils.endsWith(entry.getKey(), pathSuffix)) {
				String newPath = entry.getKey();
				bossPath = newPath.substring(0, newPath.lastIndexOf(pathSuffix));
				break;
			}
		}
		return paths.get(bossPath);
	}

	@Override
	public Iterator<Mafioso> getSubordinates(Mafioso mafioso) {
        return new ReadOnlyIterator(getSubordinates(mafioso, isDeepCount()).iterator());
	}

	@Override
	public void addSubordinate(Mafioso boss, Mafioso subordinate) {
		// First I need to find the boss in the list
		String bossSuffix = format("/%s", boss.getId());
		String subordinateId = format("/%s", subordinate.getId());
		String subordinatePath = "";
		for (Map.Entry<String, Mafioso> entry : paths.entrySet()) {
			if (StringUtils.endsWith(entry.getKey(), bossSuffix)) {
				subordinatePath = format("%s%s", entry.getKey(), subordinateId);
			} else if (StringUtils.endsWith(entry.getKey(), subordinateId)) {
				// I need to make sure that no subordinate has 2 bosses
				paths.remove(entry.getKey());
			}
		}
		// now I can add the subordinate into it's rightful place
		paths.put(subordinatePath, subordinate);
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
		for (Map.Entry<String, Mafioso> entry : paths.entrySet()) {
			if (StringUtils.endsWith(entry.getKey(), previousRelation)) {
                String subordinateId = entry.getKey();
                String bossId = StringUtils.substring(subordinateId, 0, subordinateId.lastIndexOf('/'));
				paths.remove(entry.getKey());
				promoteOldestSubordinateOf(bossId, subordinate);
				return;
			}
		}
		throw new IllegalArgumentException(format("Mafioso %s is not a subordinate of anyone", subordinate.getId()));
	}

	@Override
	public Iterator<Mafioso> iterator() {
        return new ReadOnlyIterator(paths);
	}

	@Override
	public String toString() {
		try {
            return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(paths);
		} catch (JsonProcessingException e) {
			return e.getMessage();
		}
	}

    private Map.Entry<String, Mafioso> getCupulaEntry() {
        for (Map.Entry<String, Mafioso> entry : paths.entrySet()) {
            if (entry.getKey().split("/").length == 2) {
                return entry;
            }
        }
        return null;

    }

    private List<Mafioso> getSubordinates(Mafioso mafioso, boolean goDeep) {

        List<Mafioso> subordinates = new ArrayList<>();
        String subordinateSuffix = "/" + mafioso.getId() + "/";
        for (Map.Entry<String, Mafioso> entry : paths.entrySet()) {
            if (StringUtils.contains(entry.getKey(), subordinateSuffix)) {
                // Check whether it is a direct subordinate or a indirect subordinate
                if (!goDeep) {
                    String path = entry.getKey();
                    if (!path.substring(path.lastIndexOf(subordinateSuffix) + subordinateSuffix.length())
                            .contains("/")) {
                        subordinates.add(entry.getValue());
                    }
                } else {
                    subordinates.add(entry.getValue());
                }
            }
        }
        return subordinates;
        // return new ReadOnlyIterator(subordinates.iterator());
    }

    private void promoteOldestSubordinateOf(String bossId, Mafioso oldBoss) {
        String previousRelation = format("/%s/", oldBoss.getId());
		int oldestAge = 0;
		String oldestIndex = "-/";
		List<String> relationsToChange = new ArrayList<>();
		for (Map.Entry<String, Mafioso> entry : paths.entrySet()) {
            if (StringUtils.contains(entry.getKey(), previousRelation)) {
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
			String promotedBossId = paths.get(oldestIndex).getId();
			for (String relationToChange : relationsToChange) {
				// first remove the old key
				Mafioso mafioso = paths.remove(relationToChange);
				String relation;
				if (StringUtils.equals(relationToChange, oldestIndex)) {
					relation = format("%s/%s", bossId, promotedBossId);
				} else {
                    relation = format("%s/%s/%s", bossId, promotedBossId, mafioso.getId());
				}
				paths.put(relation, mafioso);
			}
		}
	}

}
