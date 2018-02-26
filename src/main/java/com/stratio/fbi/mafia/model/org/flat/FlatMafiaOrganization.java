package com.stratio.fbi.mafia.model.org.flat;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

abstract class FlatMafiaOrganization implements MafiaOrganization {

    private static final String ROOT_KEY = "-";

    protected List<String> indexes = new ArrayList<>();

    protected List<Mafioso> mafiosos = new ArrayList<>();

    private Boolean isDeep;

    @Override
    public void setCupula(Mafioso cupula) {
        String path = format("%s/", ROOT_KEY);
        for (int i = 0; i < indexes.size(); i++) {
            if (StringUtils.startsWith(indexes.get(i), path)) {
                removeMafioso(i);
                break;
            }
        }
        // Ok, let's add it at the head of the list...
        String cupulaId = format("%s/%s", ROOT_KEY, cupula.getId());
        addMafioso(cupula, 0, cupulaId);
    }

    @Override
    public void setDeep(Boolean isDeep) {
        this.isDeep = isDeep;
    }

    @Override
    public Boolean isDeep() {
        return isDeep;
    }

    protected void addMafioso(Mafioso mafioso, int i, String index) {
        mafiosos.add(i, mafioso);
        indexes.add(i, index);
    }

    protected void removeMafioso(int i) {
        mafiosos.remove(i);
        indexes.remove(i);
    }

}
