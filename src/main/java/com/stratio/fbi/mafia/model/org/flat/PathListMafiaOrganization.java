package com.stratio.fbi.mafia.model.org.flat;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

/**
 * This class capture the whole hierarchy for each {@link Mafioso}. The key is composed of <bossId1[, bossIdN],
 * subordinateId>
 * 
 * @author rostskadat
 *
 */
public class PathListMafiaOrganization extends FlatMafiaOrganization implements MafiaOrganization {

    public List<String> getPaths() {
        return indexes;
    }

    public void setPaths(List<String> paths) {
        this.indexes = paths;
    }

    @Override
    public Mafioso getCupula() {
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.split(indexes.get(i), '/').length == 2) {
                return mafiosos.get(i);
            }
        }
        return null;
    }

    @Override
    public Mafioso getBoss(Mafioso mafioso) {
        String pathSuffix = format("/%s", mafioso.getId());
        String bossPath = null;
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.endsWith(indexes.get(i), pathSuffix)) {
                String newPath = indexes.get(i);
                bossPath = newPath.substring(0, newPath.lastIndexOf(pathSuffix));
                break;
            }
        }
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.equals(indexes.get(i), bossPath)) {
                return mafiosos.get(i);
            }
        }
        return null;
    }

    @Override
    public Iterator<Mafioso> getSubordinates(Mafioso mafioso) {
        // a subordinate is a Mafioso whosepath is deeper that the current mafioso.
        List<Mafioso> subordinates = new ArrayList<>();
        String subordinateSuffix = "/" + mafioso.getId() + "/";
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.contains(indexes.get(i), subordinateSuffix)) {
                if (!isDeep()) {
                    String path = indexes.get(i);
                    if (!path.substring(path.lastIndexOf(subordinateSuffix)).contains("/")) {
                        subordinates.add(mafiosos.get(i));
                    }
                } else {
                    subordinates.add(mafiosos.get(i));
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
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.endsWith(indexes.get(i), bossSuffix)) {
                subordinatePath = format("%s%s", indexes.get(i), subordinateId);
            } else if (StringUtils.endsWith(indexes.get(i), subordinateId)) {
                // I need to make sure that no subordinate has 2 bosses
                removeMafioso(i);
            }
        }
        // now I can add the subordinate into it's rightful place
        // XXX: rightful place does not mean anything...
        addMafioso(subordinate, 0, subordinatePath);
    }

    @Override
    public void removeSubordinate(Mafioso subordinate) {
    }

    @Override
    public Iterator<Mafioso> iterator() {
        return new PathListIterator(mafiosos);
    }

    public static class PathListIterator implements Iterator<Mafioso> {

        private Iterator<Mafioso> iterator;

        public PathListIterator(List<Mafioso> mafiosos) {
            this.iterator = mafiosos.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Mafioso next() {
            return iterator.next();
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

}
