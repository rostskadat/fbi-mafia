package com.stratio.fbi.mafia.managers;

import java.util.List;

import com.stratio.fbi.mafia.model.MafiaCell;
import com.stratio.fbi.mafia.model.Mafioso;

public interface ICosaNostraManager {

    List<Mafioso> getCapos();

    MafiaCell getCupula();

    void addSubordinate(Mafioso boss, Mafioso mafioso);

    void sendToJail(String id);

    void releaseFromJail(String id);

    void sendToCemetery(String id);

}
