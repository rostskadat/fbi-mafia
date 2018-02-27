package com.stratio.fbi.mafia.model.org;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.stratio.fbi.mafia.model.org.flat.PathListMafiaOrganizationTest;
import com.stratio.fbi.mafia.model.org.flat.RelationListMafiaOrganizationTest;
import com.stratio.fbi.mafia.model.org.tree.TreeMafiaOrganizationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ PathListMafiaOrganizationTest.class, RelationListMafiaOrganizationTest.class,
        TreeMafiaOrganizationTest.class })
public class MafiaOrganizationTestSuite {

}
