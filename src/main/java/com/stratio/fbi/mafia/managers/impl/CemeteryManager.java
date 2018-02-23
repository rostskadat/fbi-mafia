package com.stratio.fbi.mafia.managers.impl;

import org.springframework.stereotype.Component;

import com.stratio.fbi.mafia.jpa.CemeteryRepository;
import com.stratio.fbi.mafia.managers.ICemeteryManager;
import com.stratio.fbi.mafia.model.Mafioso;

@Component
public class CemeteryManager implements ICemeteryManager {

    private final CemeteryRepository cemeteryRepository;

    public CemeteryManager(final CemeteryRepository cemeteryRepository) {
        this.cemeteryRepository = cemeteryRepository;
    }

    @Override
    public void sendToCemetery(Mafioso mafioso) {
        cemeteryRepository.save(mafioso);
    }
}
