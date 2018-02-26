package com.stratio.fbi.mafia.model.org.tree;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.model.org.MafiaOrganizationTest;

public class TreeMafiaOrganizationTest extends MafiaOrganizationTest {

    @Autowired
    CosaNostraFactory factory;

    @Test
    public void testTreeMafiaOrganization() {
        testMafiaOrganization(factory.getTreeOrganization());
    }
}
