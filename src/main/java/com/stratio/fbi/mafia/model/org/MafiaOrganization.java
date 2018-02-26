package com.stratio.fbi.mafia.model.org;

import java.util.Iterator;

import com.stratio.fbi.mafia.model.Mafioso;

public interface MafiaOrganization {

    void setDeep(Boolean isDeep);

    Boolean isDeep();

    void setCupula(Mafioso mafioso);

    Mafioso getCupula();

    Mafioso getBoss(Mafioso mafioso);

    void addSubordinate(Mafioso boss, Mafioso subordinate);

    void removeSubordinate(Mafioso subordinate);

    Iterator<Mafioso> getSubordinates(Mafioso mafioso);

    Iterator<Mafioso> iterator();

}
