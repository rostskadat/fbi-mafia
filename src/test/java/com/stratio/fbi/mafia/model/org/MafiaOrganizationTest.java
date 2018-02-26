package com.stratio.fbi.mafia.model.org;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.stratio.fbi.mafia.AbstractUnitTest;
import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.model.Mafioso;

public abstract class MafiaOrganizationTest extends AbstractUnitTest {

    @Autowired
    CosaNostraFactory factory;

    @Value("${maxMafioso}")
    private Integer MAX_MAFIOSO;

    public void testMafiaOrganization(MafiaOrganization organization) {
        assertNotNull(organization);
        testIterator(organization);
        testCupula(organization);
        testSubordinate(organization);
    }

    public void testIterator(MafiaOrganization organization) {
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
        assertTrue(String.format("Was expecting %d, got %d", MAX_MAFIOSO, i), MAX_MAFIOSO == i);
    }

    public void testCupula(MafiaOrganization organization) {

        Mafioso oldCupula = organization.getCupula();
        assertNotNull(oldCupula);
        assertEquals(oldCupula, factory.createGodfather());
        organization.setCupula(oldCupula);
        Mafioso oldCupula2 = organization.getCupula();
        testMafioso(oldCupula2);
        assertEquals(oldCupula2, oldCupula);
        assertEquals(oldCupula2, factory.createGodfather());

        Mafioso newCupula = factory.createRandomMafioso();
        newCupula.setId("0");
        organization.setCupula(newCupula);
        oldCupula2 = organization.getCupula();
        testMafioso(oldCupula2);
        assertEquals(newCupula, oldCupula2);
    }

    public void testSubordinate(MafiaOrganization organization) {
        Mafioso newRecruit = factory.createRandomMafioso();
        newRecruit.setId(String.valueOf(MAX_MAFIOSO + 1 + 0));
        testMafioso(newRecruit);
        
        Mafioso cupula = organization.getCupula();
        organization.addSubordinate(cupula, newRecruit);

        Mafioso boss = organization.getBoss(newRecruit);
        testMafioso(boss);
        assertEquals(cupula, boss);

        Iterator<Mafioso> iterator = organization.getSubordinates(cupula);
        assertNotNull(iterator);
        boolean seenNewRecruit = false;
        int totalSubordinateSeen = 0;
        while (iterator.hasNext()) {
            Mafioso mafioso = iterator.next();
            testMafioso(mafioso);
            if (mafioso.equals(newRecruit)) {
                seenNewRecruit = true;
            }
            totalSubordinateSeen++;
        }
        assertTrue(seenNewRecruit);
        assertEquals(totalSubordinateSeen, (int) MAX_MAFIOSO); // All minus cupula

        organization.removeSubordinate(newRecruit);
        iterator = organization.getSubordinates(cupula);
        assertNotNull(iterator);
        totalSubordinateSeen = 0;
        while (iterator.hasNext()) {
            Mafioso mafioso = iterator.next();
            testMafioso(mafioso);
            if (mafioso.equals(newRecruit)) {
                assertFalse(true);
            }
            totalSubordinateSeen++;
        }
        assertEquals(totalSubordinateSeen, MAX_MAFIOSO - 1); // All minus cupula
    }

    private void testMafioso(Mafioso mafioso) {
        assertNotNull(mafioso);
        assertNotNull(mafioso.getFirstName());
        assertNotNull(mafioso.getLastName());
        assertNotNull(mafioso.getAge());
    }

}
