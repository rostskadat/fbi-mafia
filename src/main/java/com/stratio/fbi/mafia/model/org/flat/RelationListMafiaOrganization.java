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
 * This class only capture the relation between {@link Mafioso}. Each relation is composed of "bossId,subordinateId".
 * Look at the {@code README.md} for a comparison between the different organization.
 * 
 * @author rostskadat
 *
 */
public class RelationListMafiaOrganization extends FlatMafiaOrganization implements MafiaOrganization {

    public List<String> getRelations() {
        return indexes;
    }

    public void setRelations(List<String> relations) {
        this.indexes = relations;
    }

    @Override
    public Mafioso getCupula() {
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.startsWith(indexes.get(i), "-/")) {
                return mafiosos.get(i);
            }
        }
        return null;
    }

    @Override
    public Mafioso getBoss(Mafioso mafioso) {
        String relation = format("/%s", mafioso.getId());
        String bossRelation = null;
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.endsWith(indexes.get(i), relation)) {
                String newPath = indexes.get(i);
                bossRelation = "/" + newPath.split("/")[0];
                break;
            }
        }
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.endsWith(indexes.get(i), bossRelation)) {
                return mafiosos.get(i);
            }
        }
        return null;
    }

    @Override
    public Iterator<Mafioso> getSubordinates(Mafioso mafioso) {
        // a subordinate is a Mafioso whose relation starts with the current mafioso id.
        List<Mafioso> subordinates = new ArrayList<>();
        String subordinatePrefix = mafioso.getId() + "/";
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.startsWith(indexes.get(i), subordinatePrefix)) {
                Mafioso subordinate = mafiosos.get(i);
                subordinates.add(subordinate);
                if (isDeep()) {
                    subordinates.addAll(asList(getSubordinates(subordinate)));
                }
            }
        }
        return subordinates.iterator();
    }

    private List<Mafioso> asList(Iterator<Mafioso> iterator) {
        List<Mafioso> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    @Override
    public void addSubordinate(Mafioso boss, Mafioso subordinate) {
        String relation = format("%s/%s", boss.getId(), subordinate.getId());
        String previousRelation = format("/%s", subordinate.getId());
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.endsWith(indexes.get(i), previousRelation)) {
                // I need to make sure that no subordinate has 2 bosses
                indexes.remove(i);
                mafiosos.remove(i);
                addMafioso(subordinate, i, relation);
                return;
            }
        }
        // If not found just set it...
        addMafioso(subordinate, 0, relation);
    }

    @Override
    public void removeSubordinate(Mafioso subordinate) {
        String previousRelation = format("/%s", subordinate.getId());
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.endsWith(indexes.get(i), previousRelation)) {
                indexes.remove(i);
                // TODO: what happens to the subordinate of the other?
                return;
            }
        }
        throw new IllegalArgumentException(format("Mafioso %s is not a subordinate of anyone", subordinate.getId()));
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
