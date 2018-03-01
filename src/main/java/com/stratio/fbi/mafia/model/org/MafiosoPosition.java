package com.stratio.fbi.mafia.model.org;

import java.io.Serializable;
import java.util.List;

import com.stratio.fbi.mafia.model.Mafioso;

/**
 * 
 * 
 * @author rostskadat
 *
 */
public class MafiosoPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    private Mafioso boss;

    private Mafioso mafioso;

    private List<Mafioso> directSubordinates;

    public MafiosoPosition() {
        super();
    }

    public MafiosoPosition(Mafioso boss, Mafioso mafioso, List<Mafioso> subordinates) {
        super();
        this.boss = boss;
        this.mafioso = mafioso;
        this.directSubordinates = subordinates;
    }

    public Mafioso getBoss() {
        return boss;
    }

    public void setBoss(Mafioso boss) {
        this.boss = boss;
    }

    public Mafioso getMafioso() {
        return mafioso;
    }

    public void setMafioso(Mafioso mafioso) {
        this.mafioso = mafioso;
    }

    public List<Mafioso> getDirectSubordinates() {
        return directSubordinates;
    }

    public void setDirectSubordinates(List<Mafioso> directSubordinates) {
        this.directSubordinates = directSubordinates;
    }

}
