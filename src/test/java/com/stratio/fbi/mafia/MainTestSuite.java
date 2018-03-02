package com.stratio.fbi.mafia;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.stratio.fbi.mafia.controller.ControllersTestSuite;
import com.stratio.fbi.mafia.managers.ManagersTestSuite;
import com.stratio.fbi.mafia.model.org.MafiaOrganizationTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MafiaOrganizationTestSuite.class,
        ManagersTestSuite.class,
        ControllersTestSuite.class
})
public class MainTestSuite {

}
