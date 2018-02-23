package com.stratio.fbi.mafia.managers.impl;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.stratio.fbi.mafia.jpa.MafiosoRepository;
import com.stratio.fbi.mafia.managers.IMafiosoManager;
import com.stratio.fbi.mafia.model.Mafioso;

@Component
public class MafiosoManager extends GenericManager<Mafioso, String> implements IMafiosoManager {

    private final MafiosoRepository mafiosoRepository;

    public MafiosoManager(final MafiosoRepository mafiosoRepository) {
        this.mafiosoRepository = mafiosoRepository;
    }

    @Override
    public CrudRepository<Mafioso, String> getRepository() {
        return mafiosoRepository;
    }

    @Override
    public String getId(Mafioso entity) {
        return entity != null ? entity.getId() : null;
    }

}
