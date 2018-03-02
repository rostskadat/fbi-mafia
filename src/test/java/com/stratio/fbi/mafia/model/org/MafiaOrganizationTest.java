package com.stratio.fbi.mafia.model.org;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.stratio.fbi.mafia.AbstractUnitTest;
import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.managers.IMafiosoManagerTest;
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
        assertNotNull(organization);
        organization.erase();

        Mafioso cupula = factory.createGodfather();
        Mafioso r1 = factory.createRandomMafioso();
        Mafioso r2 = factory.createRandomMafioso();
        Mafioso r11 = factory.createRandomMafioso();
        r11.setAge(40);
        Mafioso r12 = factory.createRandomMafioso();
        r12.setAge(30);
        Mafioso r13 = factory.createRandomMafioso();
        r13.setAge(20);
        organization.setCupula(cupula);
        organization.addSubordinate(cupula, r1);
        organization.addSubordinate(cupula, r2);
        organization.addSubordinate(r1, r11);
        organization.addSubordinate(r1, r12);
        organization.addSubordinate(r1, r13);

        List<Mafioso> expected = Arrays.asList(cupula, r1, r2, r11, r12, r13);

        Iterator<Mafioso> i = organization.iterator();
        assertNotNull(i);
        while (i.hasNext()) {
            Mafioso mafioso = i.next();
            IMafiosoManagerTest.assertMafioso(mafioso);
            assertTrue(expected.contains(mafioso));
		}

        // Ok let's test the iterator behavior
        i = organization.iterator();
        assertNotNull(i);
        while (i.hasNext()) {
            i.next();
            try {
                i.remove();
                assertFalse("remove can only happens through the organization.remove()", true);
            } catch (UnsupportedOperationException e) {
                // NA
                break;
            }
        }
	}

	@Test
	public void testCupula() {
        long start = new Date().getTime();
		Mafioso alCapone = factory.createGodfather();
		Mafioso oldCupula = organization.getCupula();
		assertNotNull(oldCupula);
        IMafiosoManagerTest.assertMafiososEquals(alCapone, oldCupula);
		organization.setCupula(oldCupula);
		Mafioso oldCupula2 = organization.getCupula();
        IMafiosoManagerTest.assertMafioso(oldCupula2);
        IMafiosoManagerTest.assertMafiososEquals(alCapone, oldCupula2);

		Mafioso newCupula = factory.createRandomMafioso();
		newCupula.setId("0");
		organization.setCupula(newCupula);
		oldCupula2 = organization.getCupula();
        IMafiosoManagerTest.assertMafioso(oldCupula2);
        IMafiosoManagerTest.assertMafiososEquals(newCupula, oldCupula2);
        LOG.info(String.format("Tested cupula in %dms", new Date().getTime() - start));
	}


    @Test
    public void testMafiosoPosition() {
        organization.erase();

        Mafioso cupula = factory.createGodfather();
        Mafioso newRecruit = factory.createRandomMafioso();
        Mafioso newRecruitSibling = factory.createRandomMafioso();
        Mafioso newestRecruit = factory.createRandomMafioso();
        organization.setCupula(cupula);
        organization.addSubordinate(cupula, newRecruit);
        organization.addSubordinate(cupula, newRecruitSibling);
        organization.addSubordinate(newRecruit, newestRecruit);
        organization.addSubordinate(newRecruit, factory.createRandomMafioso());
        organization.addSubordinate(newRecruit, factory.createRandomMafioso());

        checkPosition(null, cupula, 2, newRecruit, newRecruitSibling);
        checkPosition(cupula, newRecruit, 3, newestRecruit);
        checkPosition(cupula, newRecruitSibling, 0, new Mafioso[] {});
        checkPosition(newRecruit, newestRecruit, 0, new Mafioso[] {});
    }

    private void checkPosition(Mafioso expectedBoss, Mafioso expectedMafioso,
            int expectedSubordinateSize, Mafioso... expectedSubordinates) {
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
        assertTrue(String.format("The subordinate list is incorrect: expected %d <-> got %d", expectedSubordinateSize,
        		subordinateIds.size()), expectedSubordinateSize == subordinateIds.size());
        if (expectedSubordinateSize != 0) {
            Arrays.asList(expectedSubordinates).forEach(new Consumer<Mafioso>() {
                @Override
                public void accept(Mafioso t) {
                    assertTrue(subordinateIds.contains(t.getId()));
                }
            });
        }
    }

	@Test
    public void testSubordinates() {
        organization.erase();
        Mafioso cupula = factory.createGodfather();
        Mafioso r1 = factory.createRandomMafioso();
        Mafioso r2 = factory.createRandomMafioso();
        Mafioso r11 = factory.createRandomMafioso();
        r11.setAge(40);
        Mafioso r12 = factory.createRandomMafioso();
        r12.setAge(30);
        Mafioso r13 = factory.createRandomMafioso();
        r13.setAge(20);
        organization.setCupula(cupula);
        assertSubordinates(organization, cupula, new Mafioso[] {});
        organization.addSubordinate(cupula, r1);
        assertSubordinates(organization, cupula, r1);
        organization.addSubordinate(cupula, r2);
        assertSubordinates(organization, cupula, r1, r2);
        assertSubordinates(organization, r1, new Mafioso[] {});
        assertSubordinates(organization, r2, new Mafioso[] {});
        organization.addSubordinate(r1, r11);
        assertSubordinates(organization, cupula, r1, r2, r11);
        assertSubordinates(organization, r1, r11);
        assertSubordinates(organization, r2, new Mafioso[] {});
        organization.addSubordinate(r1, r12);
        assertSubordinates(organization, cupula, r1, r2, r11, r12);
        assertSubordinates(organization, r1, r11, r12);
        assertSubordinates(organization, r2, new Mafioso[] {});
        organization.addSubordinate(r1, r13);
        assertSubordinates(organization, cupula, r1, r2, r11, r12, r13);
        assertSubordinates(organization, r1, r11, r12, r13);
        assertSubordinates(organization, r2, new Mafioso[] {});
        organization.removeFromOrganization(r1);
        assertSubordinates(organization, cupula, r2, r11, r12, r13);
        assertSubordinates(organization, r11, r12, r13);
        assertSubordinates(organization, r12, new Mafioso[] {});
        assertSubordinates(organization, r13, new Mafioso[] {});
	}

    public static void assertSubordinates(MafiaOrganization mafiaOrganization, Mafioso boss, Mafioso... mafiosos) {
        Iterator<Mafioso> iterator = mafiaOrganization.getSubordinates(boss);
		assertNotNull(iterator);
        if (mafiosos == null || mafiosos.length == 0) {
            assertFalse(iterator.hasNext());
        } else {
            List<Mafioso> expectedSubordinates = Arrays.asList(mafiosos);
            while (iterator.hasNext()) {
                Mafioso mafioso = iterator.next();
                IMafiosoManagerTest.assertMafioso(mafioso);
                assertTrue(expectedSubordinates.contains(mafioso));
            }
		}
	}
}
