package com.stratio.fbi.mafia.model.org;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.stratio.fbi.mafia.AbstractUnitTest;
import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.model.Mafioso;

public abstract class MafiaOrganizationTest extends AbstractUnitTest {

	private static final Logger LOG = LoggerFactory.getLogger(MafiaOrganizationTest.class);

	@Autowired
	CosaNostraFactory factory;

	@Value("${maxMafioso}")
	private Integer MAX_MAFIOSO;

	private MafiaOrganization organization;

	protected abstract MafiaOrganization getMafiaOrganization();

	@Before
	public void before() {
		this.organization = getMafiaOrganization();
	}

	@Test
	public void testIterator() {
		LOG.info("Testing iterator...");
		assertNotNull(organization);
		Iterator<Mafioso> iterator = organization.iterator();
		assertNotNull(iterator);
		int i = 0;
		Map<String, Mafioso> ids = new HashMap<>();
		while (iterator.hasNext()) {
			Mafioso mafioso = iterator.next();
			testMafioso(mafioso);
			assertNotNull(mafioso.getId());
			assertFalse(ids.containsKey(mafioso.getId()));
			ids.put(mafioso.getId(), mafioso);
			i++;
		}
		assertTrue(format("Was expecting %d, got %d", MAX_MAFIOSO, i), MAX_MAFIOSO == i);

		iterator = organization.iterator();
		assertNotNull(iterator);
		int totalSubordinateSeen = 0;
		while (iterator.hasNext()) {
			Mafioso mafioso = iterator.next();
			testMafioso(mafioso);
			iterator.remove();
			totalSubordinateSeen++;
		}
		assertEquals(totalSubordinateSeen, (int) MAX_MAFIOSO);
		iterator = organization.iterator();
		assertFalse(iterator.hasNext());

		LOG.info("Testing iterator... Done");
	}

	@Test
	public void testCupula() {
		LOG.info("Testing cupula...");
		Mafioso alCapone = factory.createGodfather();
		Mafioso oldCupula = organization.getCupula();
		assertNotNull(oldCupula);
		assertMafiosoEquals(alCapone, oldCupula);
		organization.setCupula(oldCupula);
		Mafioso oldCupula2 = organization.getCupula();
		testMafioso(oldCupula2);
		assertMafiosoEquals(alCapone, oldCupula2);

		Mafioso newCupula = factory.createRandomMafioso();
		newCupula.setId("0");
		organization.setCupula(newCupula);
		oldCupula2 = organization.getCupula();
		testMafioso(oldCupula2);
		assertMafiosoEquals(newCupula, oldCupula2);
		LOG.info("Testing cupula... Done");
	}

	@Test
	public void testAddSubordinate() {
		LOG.info("Testing adding subordinates...");
		Mafioso newRecruit = factory.createRandomMafioso();
		newRecruit.setId(UUID.randomUUID().toString());
		testMafioso(newRecruit);

		// Test adding to the root...
		Mafioso cupula = organization.getCupula();
		organization.addSubordinate(cupula, newRecruit);

		Mafioso boss = organization.getBoss(newRecruit);
		testMafioso(boss);
		assertEquals(cupula, boss);

		int subordinatesSeen = testSubordinate(cupula, newRecruit);
		assertEquals(subordinatesSeen, (int) MAX_MAFIOSO);

		// Adding in between...
		Mafioso newestRecruit = factory.createRandomMafioso();
		newestRecruit.setId(UUID.randomUUID().toString());
		organization.addSubordinate(newRecruit, newestRecruit);
		subordinatesSeen = testSubordinate(newRecruit, newestRecruit);
		assertEquals(subordinatesSeen, 1);

		// Adding as a leaf...
		Mafioso latestRecruit = factory.createRandomMafioso();
		latestRecruit.setId(UUID.randomUUID().toString());
		organization.addSubordinate(newestRecruit, latestRecruit);
		subordinatesSeen = testSubordinate(newestRecruit, latestRecruit);
		assertEquals(subordinatesSeen, 1);

		subordinatesSeen = testSubordinate(newRecruit, latestRecruit);
		assertEquals(subordinatesSeen, 2);
		LOG.info("Testing adding subordinates... Done");
	}

	@Test
	public void testRemoveSubordinate() {
		Mafioso newRecruit = factory.createRandomMafioso();
		newRecruit.setId(UUID.randomUUID().toString());
		testMafioso(newRecruit);
		Mafioso cupula = organization.getCupula();
		organization.addSubordinate(cupula, newRecruit);
		int initialTotalSubordinateSeen = testSubordinate(cupula, newRecruit);
		organization.remove(newRecruit);

		Iterator<Mafioso> iterator = organization.getSubordinates(cupula);
		assertNotNull(iterator);
		int totalSubordinateSeen = 0;
		while (iterator.hasNext()) {
			Mafioso mafioso = iterator.next();
			testMafioso(mafioso);
			if (mafioso.equals(newRecruit)) {
				assertTrue(false);
			}
			totalSubordinateSeen++;
		}
		assertEquals(initialTotalSubordinateSeen, totalSubordinateSeen + 1);
	}

	private int testSubordinate(Mafioso boss, Mafioso subordinate) {
		Iterator<Mafioso> iterator = organization.getSubordinates(boss);
		assertNotNull(iterator);
		boolean seenSubordinate = false;
		int totalSubordinateSeen = 0;
		while (iterator.hasNext()) {
			Mafioso mafioso = iterator.next();
			testMafioso(mafioso);
			if (mafioso.equals(subordinate)) {
				seenSubordinate = true;
			}
			totalSubordinateSeen++;
		}
		assertTrue(seenSubordinate);
		return totalSubordinateSeen;

	}

	private void assertMafiosoEquals(Mafioso m1, Mafioso m2) {
		assertNotNull(m1);
		assertNotNull(m2);
		assertEquals(m1.getFirstName(), m2.getFirstName());
		assertEquals(m1.getLastName(), m2.getLastName());
		assertEquals(m1.getAge(), m2.getAge());
	}

	private void testMafioso(Mafioso mafioso) {
		assertNotNull(mafioso);
		assertNotNull(mafioso.getFirstName());
		assertNotNull(mafioso.getLastName());
		assertNotNull(mafioso.getAge());
	}

}
