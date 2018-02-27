package com.stratio.fbi.mafia.managers;

import java.util.Iterator;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.stratio.fbi.mafia.AbstractUnitTest;
import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

public class ICosaNostraManagerTest extends AbstractUnitTest {

	@Autowired
	ICosaNostraManager cosaNostraManager;

	@Autowired
	CosaNostraFactory factory;

	@Value("${maxMafioso}")
	private Integer MAX_MAFIOSO;

	@Test
	public void testGetCapos() {
		MafiaOrganization organization = factory.getTreeOrganization();
		assertNotNull(organization);
		Iterator<Mafioso> iterator = organization.getSubordinates(organization.getCupula());
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		int i = 0;
		while (iterator.hasNext()) {
			Mafioso mafioso = iterator.next();
			assertNotNull(mafioso);
			i++;
		}
		assertTrue(MAX_MAFIOSO == i);

	}
}
