package com.stratio.fbi.mafia.model.org.flat;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

/**
 * This class capture the whole hierarchy for each {@link Mafioso}. The key is
 * composed of <bossId1[, bossIdN], subordinateId>
 * 
 * @author rostskadat
 *
 */
public class PathListMafiaOrganization implements MafiaOrganization {

	private static final String ROOT_KEY = "-";

	protected Map<String, Mafioso> paths = new HashMap<>();

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
	public void setCupula(Mafioso cupula) {
		String path = format("%s/", ROOT_KEY);
		for (Map.Entry<String, Mafioso> entry : paths.entrySet()) {
			if (StringUtils.startsWith(entry.getKey(), path)) {
				paths.remove(entry.getKey());
				break;
			}
		}
		// Ok, let's add it at the head of the list...
		paths.put(format("%s/%s", ROOT_KEY, cupula.getId()), cupula);
	}

	@Override
	public Mafioso getCupula() {
		for (Map.Entry<String, Mafioso> entry : paths.entrySet()) {
			if (entry.getKey().split("/").length == 2) {
				return entry.getValue();
			}
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
		// a subordinate is a Mafioso whosepath is deeper that the current
		// mafioso.
		List<Mafioso> subordinates = new ArrayList<>();
		String subordinateSuffix = "/" + mafioso.getId() + "/";
		for (Map.Entry<String, Mafioso> entry : paths.entrySet()) {
			if (StringUtils.contains(entry.getKey(), subordinateSuffix)) {
				if (!isDeep()) {
					String path = entry.getKey();
					if (!path.substring(path.lastIndexOf(subordinateSuffix)).contains("/")) {
						subordinates.add(entry.getValue());
					}
				} else {
					subordinates.add(entry.getValue());
				}
			}
		}
		return subordinates.iterator();
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
	public void remove(Mafioso subordinate) {
		String previousRelation = format("/%s", subordinate.getId());
		for (Map.Entry<String, Mafioso> entry : paths.entrySet()) {
			if (StringUtils.endsWith(entry.getKey(), previousRelation)) {
				String[] pathElements = entry.getKey().split("/");
				String bossId = StringUtils.join(Arrays.copyOfRange(pathElements, 0, pathElements.length - 1));
				paths.remove(entry.getKey());
				promoteOldestSubordinateOf(bossId, subordinate);
				return;
			}
		}
		throw new IllegalArgumentException(format("Mafioso %s is not a subordinate of anyone", subordinate.getId()));
	}

	@Override
	public Iterator<Mafioso> iterator() {
		return new PathListIterator(paths);
	}

	private static class PathListIterator implements Iterator<Mafioso> {

		private Iterator<Mafioso> iterator;

		public PathListIterator(Map<String, Mafioso> paths) {
			this.iterator = paths.values().iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Mafioso next() {
			return iterator.next();
		}

		@Override
		public void remove() {
			iterator.remove();
		}

	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return e.getMessage();
		}
	}

	private void promoteOldestSubordinateOf(String bossId, Mafioso oldBoss) {
		String previousRelation = format("%s/", oldBoss.getId());
		int oldestAge = 0;
		String oldestIndex = "-/";
		List<String> relationsToChange = new ArrayList<>();
		for (Map.Entry<String, Mafioso> entry : paths.entrySet()) {
			if (StringUtils.startsWith(entry.getKey(), previousRelation)) {
				// Ok find out who is the oldest among the subordinate
				if (entry.getValue().getAge() > oldestAge) {
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
					relation = format("%s/%s", promotedBossId, mafioso.getId());
				}
				paths.put(relation, mafioso);
			}
		}
	}
}
