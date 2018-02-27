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
import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.exception.ResourceNotFoundException;
import com.stratio.fbi.mafia.model.Mafioso;

public class IMafiosoManagerTest extends AbstractUnitTest {

	@Autowired
	IMafiosoManager mafiosoManager;

	@Autowired
	CacheManager cacheManager;

	@Autowired
	CosaNostraFactory factory;

	@Test
	public void testAddMafioso() {
		Mafioso alCapone = factory.createGodfather();
		Mafioso mafioso = mafiosoManager.add(alCapone);
		assertMafiosoEquals(alCapone, mafioso);
		mafioso = mafiosoManager.get(mafioso.getId());
		assertMafiosoEquals(alCapone, mafioso);
		int i = 0;
		for (; i < 100; i++) {
			// this one should hit the cache
			mafioso = mafiosoManager.get(mafioso.getId());
			assertMafiosoEquals(alCapone, mafioso);
		}
		assertAttributeHasValue("CacheHits", i);
	}

	@Test
	public void testUpdateMafioso() {
		Mafioso alCapone = factory.createGodfather();
		Mafioso mafioso = mafiosoManager.add(alCapone);
		assertTrue(mafiosoManager.exists(mafioso.getId()));
		assertMafiosoEquals(alCapone, mafioso);
		mafioso = mafiosoManager.get(mafioso.getId());
		assertMafiosoEquals(alCapone, mafioso);
		mafioso.setFirstName("Alphonse Gabriel");
		mafiosoManager.update(mafioso);
		alCapone = mafiosoManager.get(mafioso.getId());
		assertMafiosoEquals(alCapone, mafioso);
		assertEquals(alCapone.getFirstName(), "Alphonse Gabriel");
	}

	@Test
	public void testDeleteMafioso() {
		Mafioso alCapone = factory.createGodfather();
		Mafioso mafioso = mafiosoManager.add(alCapone);
		assertTrue(mafiosoManager.exists(mafioso.getId()));
		assertMafiosoEquals(alCapone, mafioso);
		mafioso = mafiosoManager.get(mafioso.getId());
		assertMafiosoEquals(alCapone, mafioso);
		mafiosoManager.delete(mafioso.getId());
		try {
			mafiosoManager.get(alCapone.getId());
			assertTrue("Should Have thrown a ResourceNotFoundException", false);
		} catch (ResourceNotFoundException e) {
			assertTrue(true);
		}
	}

	private void assertMafiosoEquals(Mafioso m1, Mafioso m2) {
		assertNotNull(m1);
		assertNotNull(m2);
		assertEquals(m1.getFirstName(), m2.getFirstName());
		assertEquals(m1.getLastName(), m2.getLastName());
		assertEquals(m1.getAge(), m2.getAge());
	}

	private void assertAttributeHasValue(String attribute, long value) {
		Cache cache = cacheManager.getCache(SimpleCacheConfig.GENERIC_CACHE);
		List<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
		try {
			String confFile = SimpleCacheConfig.GENERIC_CACHE_URI.toURI().toString().replaceAll(":", ".");
			ObjectName objectName = new ObjectName(String
			        .format("javax.cache:type=CacheStatistics,CacheManager=%s,Cache=%s", confFile, cache.getName()));
			Long stat = (Long) mBeanServers.get(0).getAttribute(objectName, attribute);
			assertEquals(Long.valueOf(value), stat);
		} catch (MalformedObjectNameException | InstanceNotFoundException | AttributeNotFoundException
		        | ReflectionException | MBeanException | URISyntaxException e) {
			assertTrue(e.getMessage(), false);
		}

	}
}
