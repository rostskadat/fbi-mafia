package com.stratio.fbi.mafia.managers;

import static com.stratio.fbi.mafia.managers.IMafiosoManagerTest.assertMafioso;
import static com.stratio.fbi.mafia.managers.IMafiosoManagerTest.assertMafiososEquals;
import static com.stratio.fbi.mafia.model.org.MafiaOrganizationTest.assertSubordinates;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.stratio.fbi.mafia.AbstractUnitTest;
import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.managers.ICosaNostraManager.OrganizationType;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

public class ICosaNostraManagerTest extends AbstractUnitTest {

    private static final String INVALID_ORGANIZATION_TYPE = "Cant't run unit test without any organization. Check the organizationType property in your property file";

    @Autowired
    ICosaNostraManager cosaNostra;

    @Autowired
    CosaNostraFactory factory;

    @Value("${maxMafioso}")
    private Integer MAX_MAFIOSO;

    @Test
    public void getOrganization() {
        if (OrganizationType.NONE != cosaNostra.getOrganizationType()) {
            MafiaOrganization organization = cosaNostra.getOrganization();
            assertNotNull(organization);
            assertMafioso(organization.getCupula());
        } else {
            assertTrue(INVALID_ORGANIZATION_TYPE, false);
        }
    }

    @Test
    public void getWatchList() {
        if (OrganizationType.NONE != cosaNostra.getOrganizationType()) {
            
            factory.createFixedOrganization(cosaNostra.getOrganization(), true);
            
            List<Mafioso> mafiosos = cosaNostra.getWatchList();
            assertNotNull(mafiosos);
            mafiosos.forEach(new Consumer<Mafioso>() {
                @Override
                public void accept(Mafioso t) {
                    assertMafioso(t);
                    int count = cosaNostra.getOrganization().getSubordinateCount(t);
                    assertTrue(count >= cosaNostra.getWatchThreshold());
                }
            });
        } else {
            assertTrue(INVALID_ORGANIZATION_TYPE, false);
        }
    }

    @Test
    public void testJail() {
        if (OrganizationType.NONE != cosaNostra.getOrganizationType()) {

            MafiaOrganization organization = cosaNostra.getOrganization();
            factory.createFixedOrganization(organization, true);
            Mafioso oldCupula = organization.getCupula();
            String oldCupulaId = oldCupula.getId();

            cosaNostra.sendToJail(oldCupulaId);
            Mafioso newCupula = organization.getCupula();
            String newCupulaId = newCupula.getId();
            assertFalse(StringUtils.equals(oldCupulaId, newCupulaId));

            cosaNostra.releaseFromJail(oldCupulaId);
            Mafioso newestCupula = organization.getCupula();
            assertMafiososEquals(oldCupula, newestCupula);

            // and then some random guy within the organization
            organization.erase();

            Mafioso cupula = factory.createGodfather(true);
            Mafioso r1 = factory.createRandomMafioso(true);
            Mafioso r2 = factory.createRandomMafioso(true);
            Mafioso r11 = factory.createRandomMafioso(true);
            r11.setAge(40);
            Mafioso r12 = factory.createRandomMafioso(true);
            r12.setAge(30);
            Mafioso r13 = factory.createRandomMafioso(true);
            r13.setAge(20);
            organization.setCupula(cupula);
            organization.addSubordinate(cupula, r1);
            organization.addSubordinate(cupula, r2);
            organization.addSubordinate(r1, r11);
            organization.addSubordinate(r1, r12);
            organization.addSubordinate(r1, r13);

            cosaNostra.sendToJail(r2.getId());
            assertMafiososEquals(organization.getCupula(), cupula);
            assertEquals(4, organization.getSubordinateCount(organization.getCupula()));
            assertSubordinates(organization, organization.getCupula(), r1, r11, r12, r13);

            cosaNostra.sendToJail(cupula.getId());
            assertMafiososEquals(organization.getCupula(), r1);
            assertEquals(3, organization.getSubordinateCount(organization.getCupula()));
            assertSubordinates(organization, organization.getCupula(), r11, r12, r13);

            cosaNostra.sendToJail(r1.getId());
            assertMafiososEquals(organization.getCupula(), r11);
            assertEquals(2, organization.getSubordinateCount(organization.getCupula()));
            assertSubordinates(organization, organization.getCupula(), r12, r13);

        } else {
            assertTrue(INVALID_ORGANIZATION_TYPE, false);
        }
    }
    
    
}
