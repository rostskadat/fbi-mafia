package com.stratio.fbi.mafia.managers.impl;

import org.springframework.stereotype.Component;

import com.stratio.fbi.mafia.exception.ResourceNotFoundException;
import com.stratio.fbi.mafia.jpa.JailRepository;
import com.stratio.fbi.mafia.managers.IJailManager;
import com.stratio.fbi.mafia.model.Mafioso;

@Component
public class JailManager implements IJailManager {


    private final JailRepository jailRepository;

    public JailManager(final JailRepository jailRepository) {
        this.jailRepository = jailRepository;
    }

    @Override
    public void sendToJail(Mafioso mafioso) {
        jailRepository.save(mafioso);
    }

    @Override
    public Mafioso releaseFromJail(String id) {
        if (jailRepository.exists(id)) {
            Mafioso mafioso = jailRepository.getOne(id);
            jailRepository.delete(id);
            return mafioso;
        }
        throw new ResourceNotFoundException("'id' doesn't exists: " + id);
    }

}
