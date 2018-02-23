package com.stratio.fbi.mafia.managers;

import com.stratio.fbi.mafia.model.Mafioso;

public interface IJailManager {

    void sendToJail(Mafioso mafioso);

    Mafioso releaseFromJail(String id);

}
