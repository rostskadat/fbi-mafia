package com.stratio.fbi.mafia.managers.impl;

import org.springframework.stereotype.Component;

import com.stratio.fbi.mafia.exception.ResourceNotFoundException;
import com.stratio.fbi.mafia.jpa.JailRepository;
import com.stratio.fbi.mafia.managers.IJailManager;
import com.stratio.fbi.mafia.model.org.MafiosoPosition;

@Component
public class JailManager implements IJailManager {


    private final JailRepository jailRepository;

    public JailManager(final JailRepository jailRepository) {
        this.jailRepository = jailRepository;
    }

    @Override
    public boolean exists(String id) {
        return jailRepository.exists(id);
    }

    @Override
    public void sendToJail(MafiosoPosition position) {
        jailRepository.save(position);
    }

    @Override
    public MafiosoPosition releaseFromJail(String id) {
        if (jailRepository.exists(id)) {
        		MafiosoPosition position = jailRepository.getOne(id);
            jailRepository.delete(id);
            return position;
        }
        throw new ResourceNotFoundException("'id' doesn't exists: " + id);
    }

}
