package com.stratio.fbi.mafia.managers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import com.stratio.fbi.mafia.AbstractUnitTest;
import com.stratio.fbi.mafia.config.SimpleCacheConfig;
import com.stratio.fbi.mafia.model.Mafioso;

public class IMafiosoManagerTest extends AbstractUnitTest {

    private static final Log LOG = LogFactory.getLog(IMafiosoManagerTest.class);

    @Autowired
    IMafiosoManager mafiosoManager;

    @Autowired
    CacheManager cache;

    @Test
    public void addMafioso() {
        Mafioso alCapone = new Mafioso();
        alCapone.setFirstName("Al");
        alCapone.setLastName("Capone");
        alCapone.setAge(48);
        Mafioso mafioso = mafiosoManager.add(alCapone);
        assertEquals(alCapone, mafioso);
        mafioso = mafiosoManager.get(alCapone.getId());
        assertEquals(alCapone, mafioso);
        mafioso = mafiosoManager.get(alCapone.getId());
        assertEquals(alCapone, mafioso);
        LOG.info(String.format("Cache Hits=%s", cache.getCache(SimpleCacheConfig.GENERIC_CACHE)));
        // The current size of the cache (cache.xxx.size)
        // Hit ratio (cache.xxx.hit.ratio)
        // Miss ratio (cache.xxx.miss.ratio)
    }
}
