package com.stratio.fbi.mafia.managers;

import javax.validation.constraints.NotNull;

import com.stratio.fbi.mafia.model.org.MafiosoPosition;

public interface IJailManager {

    boolean exists(@NotNull String id);

    void sendToJail(MafiosoPosition position);

    MafiosoPosition releaseFromJail(String id);

}
