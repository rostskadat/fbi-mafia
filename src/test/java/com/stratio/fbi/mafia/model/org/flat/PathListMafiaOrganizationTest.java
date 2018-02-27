package com.stratio.fbi.mafia.model.org.flat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;
import com.stratio.fbi.mafia.model.org.MafiaOrganizationTest;

public class PathListMafiaOrganizationTest extends MafiaOrganizationTest {

	private static final Logger LOG = LoggerFactory.getLogger(PathListMafiaOrganizationTest.class);

	@Autowired
	CosaNostraFactory factory;

	@Override
	protected MafiaOrganization getMafiaOrganization() {
		LOG.debug("Creating random mafia oragnization...");
		return factory.getPathListOrganization();
	}
}
