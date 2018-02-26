package com.stratio.fbi.mafia.managers.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.stratio.fbi.mafia.config.SimpleCacheConfig;
import com.stratio.fbi.mafia.exception.ResourceNotFoundException;
import com.stratio.fbi.mafia.jpa.MafiosoRepository;
import com.stratio.fbi.mafia.managers.IMafiosoManager;
import com.stratio.fbi.mafia.model.Mafioso;

/**
 * This class acts as an access point for all the known, available {@link Mafioso}
 * 
 * @author rostskadat
 *
 */
@Component
@CacheConfig(cacheNames = SimpleCacheConfig.GENERIC_CACHE)
public class MafiosoManager implements IMafiosoManager {

    private final MafiosoRepository mafiosoRepository;

    public MafiosoManager(final MafiosoRepository mafiosoRepository) {
        this.mafiosoRepository = mafiosoRepository;
    }

    public String getId(Mafioso entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    public Iterable<Mafioso> findAll() {
        return mafiosoRepository.findAll();
    }

    @Override
    @Cacheable(key = "#id")
    public Mafioso get(String id) {
        if (mafiosoRepository.exists(id)) {
            return mafiosoRepository.findOne(id);
        }
        throw new ResourceNotFoundException("'id' doesn't exists: " + id);
    }

    @Override
    public boolean exists(String id) {
        return mafiosoRepository.exists(id);
    }

    @Override
    public Mafioso add(Mafioso mafioso) {
        return mafiosoRepository.save(mafioso);
    }

    @Override
    @CacheEvict(key = "#id")
    public void delete(String id) {
        if (mafiosoRepository.exists(id)) {
            mafiosoRepository.delete(id);
        } else {
            throw new ResourceNotFoundException("'id' doesn't exists: " + id);
        }
    }

    @Override
    @CacheEvict(key = "#mafioso.id")
    public void update(Mafioso mafioso) {
        if (mafiosoRepository.exists(getId(mafioso))) {
            mafiosoRepository.save(mafioso);
        } else {
            throw new ResourceNotFoundException("Invalid mafioso " + mafioso);
        }
    }

}
