package com.stratio.fbi.mafia.model.org;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
        long start = new Date().getTime();
		this.organization = getMafiaOrganization();
        LOG.info(String.format("Created organization %s in %dms", this.organization.getClass().getSimpleName(),
                new Date().getTime() - start));
	}

	@Test
	public void testIterator() {
        long start = new Date().getTime();
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
			totalSubordinateSeen++;
		}
		assertEquals(totalSubordinateSeen, (int) MAX_MAFIOSO);

        // Ok let's test the iterator behavior
		iterator = organization.iterator();
        assertNotNull(iterator);
        while (iterator.hasNext()) {
            iterator.next();
            try {
                iterator.remove();
                assertFalse("remove can only happens through the organization.remove()", true);
            } catch (UnsupportedOperationException e) {
                // NA
                break;
            }
        }
        LOG.info(String.format("Tested iterator in %dms", new Date().getTime() - start));
	}

	@Test
	public void testCupula() {
        long start = new Date().getTime();
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
        LOG.info(String.format("Tested cupula in %dms", new Date().getTime() - start));
	}

	@Test
	public void testAddSubordinate() {
        long start = new Date().getTime();
		Mafioso newRecruit = factory.createRandomMafioso();
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
		organization.addSubordinate(newRecruit, newestRecruit);
		subordinatesSeen = testSubordinate(newRecruit, newestRecruit);
		assertEquals(subordinatesSeen, 1);

		// Adding as a leaf...
		Mafioso latestRecruit = factory.createRandomMafioso();
		organization.addSubordinate(newestRecruit, latestRecruit);
		subordinatesSeen = testSubordinate(newestRecruit, latestRecruit);
		assertEquals(subordinatesSeen, 1);

		subordinatesSeen = testSubordinate(newRecruit, latestRecruit);
		assertEquals(subordinatesSeen, 2);
        LOG.info(String.format("Tested adding subordinates in %dms", new Date().getTime() - start));
	}

    @Test
    public void testMafiosoPosition() {
        organization.erase();

        Mafioso cupula = factory.createGodfather();
        Mafioso newRecruit = factory.createRandomMafioso();
        Mafioso newestRecruit = factory.createRandomMafioso();
        organization.setCupula(cupula);
        organization.addSubordinate(cupula, newRecruit);
        organization.addSubordinate(newRecruit, newestRecruit);
        organization.addSubordinate(newRecruit, factory.createRandomMafioso());
        organization.addSubordinate(newRecruit, factory.createRandomMafioso());

        checkPosition(null, cupula, 1, newRecruit);
        checkPosition(cupula, newRecruit, 3, newestRecruit);
        checkPosition(newRecruit, newestRecruit, 0, null);
    }

    private void checkPosition(Mafioso expectedBoss, Mafioso expectedMafioso,
            int expectedSubordinateSize, Mafioso expectedSubordinate) {
        MafiosoPosition position = organization.getMafiosoPosition(expectedMafioso);
        assertNotNull(position);
        if (expectedBoss == null) {
            assertNull(position.getBossId());
        } else {
            assertNotNull(position.getBossId());
            assertEquals(position.getBossId(), expectedBoss.getId());
        }
        assertNotNull(position.getMafiosoId());
        assertEquals(position.getMafiosoId(), expectedMafioso.getId());

        List<String> subordinateIds = position.getDirectSubordinateIds();
        assertNotNull(subordinateIds);
        assertTrue(String.format("The subordinate list is incorrect expected %d <-> got %d", expectedSubordinateSize,
        		subordinateIds.size()), expectedSubordinateSize == subordinateIds.size());
        if (expectedSubordinateSize != 0) {
            boolean seenSubordinate = false;
            for (String subordinateId : subordinateIds) {
                if (StringUtils.equals(subordinateId, expectedSubordinate.getId())) {
                    seenSubordinate = true;
                }
            }
            assertTrue(seenSubordinate);
        }
    }

	@Test
	public void testRemoveSubordinate() {
        // TODO: Check that it also work for the cupula
        long start = new Date().getTime();
		Mafioso newRecruit = factory.createRandomMafioso();
		testMafioso(newRecruit);
		Mafioso cupula = organization.getCupula();
		organization.addSubordinate(cupula, newRecruit);
		int initialTotalSubordinateSeen = testSubordinate(cupula, newRecruit);
        assertEquals(initialTotalSubordinateSeen, (int) MAX_MAFIOSO);
        organization.removeFromOrganization(newRecruit);

		Iterator<Mafioso> iterator = organization.getSubordinates(cupula);
		assertNotNull(iterator);
		int totalSubordinateSeen = 0;
		while (iterator.hasNext()) {
			Mafioso mafioso = iterator.next();
			testMafioso(mafioso);
            if (StringUtils.equals(mafioso.getId(), newRecruit.getId())) {
				assertTrue(false);
			}
			totalSubordinateSeen++;
		}
		assertEquals(initialTotalSubordinateSeen, totalSubordinateSeen + 1);

        // Ok let's check that we promote the oldest of the subordinate
        iterator = organization.getSubordinates(cupula);
        assertNotNull(iterator);
        while (iterator.hasNext()) {
            iterator.next();
            try {
                iterator.remove();
                assertFalse("remove can only happens through the organization.remove()", true);
            } catch (UnsupportedOperationException e) {
                // NA
                break;
            }
        }

        // I create 2 level, it's easier to check...
        Mafioso intermediaryBoss = factory.createRandomMafioso();
        intermediaryBoss.setAge(50);
        Mafioso intermediaryCapo = factory.createRandomMafioso();
        intermediaryCapo.setAge(40);
        organization.addSubordinate(cupula, intermediaryBoss);
        organization.addSubordinate(intermediaryBoss, intermediaryCapo);
        newRecruit = factory.createRandomMafioso();
        newRecruit.setAge(20);
        organization.addSubordinate(intermediaryCapo, newRecruit);
        Mafioso futureCapo = factory.createRandomMafioso();
        futureCapo.setAge(30);
        organization.addSubordinate(intermediaryCapo, futureCapo);
        organization.removeFromOrganization(intermediaryCapo);
        iterator = organization.getSubordinates(intermediaryBoss);
        assertNotNull(iterator);
        totalSubordinateSeen = 0;
        while (iterator.hasNext()) {
            Mafioso mafioso = iterator.next();
            testMafioso(mafioso);
            totalSubordinateSeen++;
        }
        assertEquals(totalSubordinateSeen, 2);
        iterator = organization.getSubordinates(futureCapo);
        assertNotNull(iterator);
        totalSubordinateSeen = 0;
        while (iterator.hasNext()) {
            Mafioso mafioso = iterator.next();
            testMafioso(mafioso);
            assertEquals((int) mafioso.getAge(), 20);
            totalSubordinateSeen++;
        }
        assertEquals(totalSubordinateSeen, 1);
        LOG.info(String.format("Tested removing subordinates in %dms", new Date().getTime() - start));
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
