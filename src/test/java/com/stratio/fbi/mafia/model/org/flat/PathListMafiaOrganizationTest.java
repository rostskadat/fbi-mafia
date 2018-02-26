package com.stratio.fbi.mafia.model.org.flat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.model.org.MafiaOrganizationTest;

public class PathListMafiaOrganizationTest extends MafiaOrganizationTest {

    @Autowired
    CosaNostraFactory factory;

    @Test
    public void testMafiaOrganization() {
        testMafiaOrganization(factory.getPathListOrganization());
    }

}
