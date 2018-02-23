package com.stratio.fbi.mafia.managers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.stratio.fbi.mafia.AbstractUnitTest;
import com.stratio.fbi.mafia.demo.RandomCosaNostra;
import com.stratio.fbi.mafia.model.MafiaCell;
import com.stratio.fbi.mafia.model.Mafioso;

public class ICosaNostraManagerTest extends AbstractUnitTest {

    private static final Log LOG = LogFactory.getLog(ICosaNostraManagerTest.class);

    @Autowired
    RandomCosaNostra randomCosaNostra;

    @Autowired
    ICosaNostraManager cosaNostraManager;

    @Test
    public void testGetCapos() {
        List<Mafioso> capos = cosaNostraManager.getCapos();
        assertNotNull(capos);
        assertTrue(capos.size() > 0);
        for (Mafioso capo : capos) {
            assertNotNull(capo);
        }
    }
    
    @Test
    public void getCurrentOrganization() {
        MafiaCell mafiaCell = cosaNostraManager.getCupula();
        assertNotNull(mafiaCell);
        assertTrue(mafiaCell.getSubordinates().size() > 0);
    }
}
