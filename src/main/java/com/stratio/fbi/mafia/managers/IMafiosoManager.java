package com.stratio.fbi.mafia.managers;

import javax.validation.constraints.NotNull;

import com.stratio.fbi.mafia.model.Mafioso;

public interface IMafiosoManager {

    Mafioso get(@NotNull String id);

    Mafioso add(@NotNull Mafioso mafioso);

    void delete(@NotNull String id);

    void update(@NotNull Mafioso mafioso);

}
