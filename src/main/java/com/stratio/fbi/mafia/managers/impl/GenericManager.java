package com.stratio.fbi.mafia.managers.impl;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import com.stratio.fbi.mafia.config.SimpleCacheConfig;
import com.stratio.fbi.mafia.exception.ResourceNotFoundException;

@CacheConfig(cacheNames = SimpleCacheConfig.GENERIC_CACHE)
public abstract class GenericManager<T, I extends Serializable> {

    private static final Log LOG = LogFactory.getLog(GenericManager.class);

    public abstract CrudRepository<T, I> getRepository();

    public abstract I getId(T entity);

    public Iterable<T> findAll() {
        return getRepository().findAll();
    }

    @Cacheable(key = "#id")
    public T get(I id) {
        LOG.info("Retrieving resource " + id);
        return getRepository().findOne(id);
    }

    public T add(T entity) {
        LOG.info("Adding resource " + entity);
        return getRepository().save(entity);
    }

    @CacheEvict(key = "#id")
    public void delete(I id) {
        if (getRepository().exists(id)) {
            LOG.info("Deleting resource " + id);
            getRepository().delete(id);
        } else {
            throw new ResourceNotFoundException("'id' doesn't exists: " + id);
        }
    }

    @CacheEvict(key = "#{entity.id}")
    public void update(T entity) {
        if (getRepository().exists(getId(entity))) {
            LOG.info("Updating resource " + getId(entity));
            getRepository().save(entity);
        } else {
            throw new ResourceNotFoundException("Invalid resource " + entity);
        }
    }
}
