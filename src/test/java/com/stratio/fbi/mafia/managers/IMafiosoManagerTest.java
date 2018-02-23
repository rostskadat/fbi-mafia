package com.stratio.fbi.mafia.managers;

import java.net.URISyntaxException;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.stratio.fbi.mafia.AbstractUnitTest;
import com.stratio.fbi.mafia.config.SimpleCacheConfig;
import com.stratio.fbi.mafia.demo.RandomCosaNostra;
import com.stratio.fbi.mafia.exception.ResourceNotFoundException;
import com.stratio.fbi.mafia.model.Mafioso;

public class IMafiosoManagerTest extends AbstractUnitTest {

    @Autowired
    RandomCosaNostra randomCosaNostra;

    @Autowired
    IMafiosoManager mafiosoManager;

    @Autowired
    CacheManager cacheManager;

    @Test
    public void testAddMafioso() {
        Mafioso alCapone = new Mafioso();
        alCapone.setFirstName("Al");
        alCapone.setLastName("Capone");
        alCapone.setAge(48);
        Mafioso mafioso = mafiosoManager.add(alCapone);
        assertEquals(alCapone, mafioso);
        mafioso = mafiosoManager.get(alCapone.getId());
        assertEquals(alCapone, mafioso);
        int i = 0;
        for (; i < 100; i++) {
            // this one should hit the cache
            mafioso = mafiosoManager.get(alCapone.getId());
            assertEquals(alCapone, mafioso);
        }
        assertAttributeHasValue("CacheHits", i);
    }

    @Test
    public void testUpdateMafioso() {
        assertTrue(mafiosoManager.exists("1"));
        Mafioso alCapone = new Mafioso();
        alCapone.setFirstName("Al");
        alCapone.setLastName("Capone");
        alCapone.setAge(48);
        Mafioso mafioso = mafiosoManager.add(alCapone);
        assertEquals(alCapone, mafioso);
        mafioso = mafiosoManager.get(alCapone.getId());
        assertEquals(alCapone, mafioso);
        alCapone.setFirstName("Alphonse Gabriel");
        mafiosoManager.update(alCapone);
        mafioso = mafiosoManager.get(alCapone.getId());
        assertEquals(alCapone, mafioso);
        assertEquals(alCapone.getFirstName(), "Alphonse Gabriel");
    }

    @Test
    public void testDeleteMafioso() {
        assertTrue(mafiosoManager.exists("1"));
        Mafioso alCapone = new Mafioso();
        alCapone.setFirstName("Al");
        alCapone.setLastName("Capone");
        alCapone.setAge(48);
        Mafioso mafioso = mafiosoManager.add(alCapone);
        assertEquals(alCapone, mafioso);
        mafioso = mafiosoManager.get(alCapone.getId());
        assertEquals(alCapone, mafioso);
        mafiosoManager.delete(alCapone.getId());
        try {
            mafiosoManager.get(alCapone.getId());
            assertTrue("Should Have thrown a ResourceNotFoundException", false);
        } catch (ResourceNotFoundException e) {
            assertTrue(true);
        }
    }

    private void assertAttributeHasValue(String attribute, long value) {
        Cache cache = cacheManager.getCache(SimpleCacheConfig.GENERIC_CACHE);
        List<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        try {
            String confFile = SimpleCacheConfig.GENERIC_CACHE_URI.toURI().toString().replaceAll(":", ".");
            ObjectName objectName = new ObjectName(
                    String.format("javax.cache:type=CacheStatistics,CacheManager=%s,Cache=%s",
                            confFile, cache.getName()));
            Long stat = (Long) mBeanServers.get(0).getAttribute(objectName, attribute);
            assertEquals(Long.valueOf(value), stat);
        } catch (MalformedObjectNameException | InstanceNotFoundException | AttributeNotFoundException
                | ReflectionException | MBeanException | URISyntaxException e) {
            assertTrue(e.getMessage(), false);
        }

    }
}
