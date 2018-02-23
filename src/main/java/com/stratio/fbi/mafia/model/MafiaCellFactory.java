package com.stratio.fbi.mafia.model;

public class MafiaCellFactory {

    private MafiaCellFactory() {
        // NA
    }

    public static MafiaCell createMafiaCell(Mafioso mafioso) {
        return new MafiaCell(mafioso);
    }

    public static MafiaCell createMember(Mafioso mafioso, Mafioso... subordinates) {
        MafiaCell cell = new MafiaCell(mafioso);
        if (subordinates != null) {
            for (Mafioso subordinate : subordinates) {
                cell.addSubordinate(subordinate);
            }
        }
        return cell;
    }

    public static MafiaCell createMember(Mafioso boss, Mafioso mafioso, Mafioso... subordinates) {
        MafiaCell cell = createMember(mafioso, subordinates);
        cell.setBoss(boss);
        return cell;
    }
}
