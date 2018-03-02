package com.stratio.fbi.mafia.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.fbi.mafia.AbstractControllerTest;
import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.managers.ICosaNostraManager;
import com.stratio.fbi.mafia.managers.IMafiosoManagerTest;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

public class CosaNostraControllerTest extends AbstractControllerTest {

    private static final String COSA_NOSTRA_API_URI = "/api/cosaNostra";
    private static final String URL_GET_ORGA = COSA_NOSTRA_API_URI + "/getOrganization";
    private static final String URL_GET_WATCH_LIST = COSA_NOSTRA_API_URI + "/getWatchList";
    private static final String URL_SEND_TO_JAIL = COSA_NOSTRA_API_URI + "/sendToJail/{id}";
    private static final String URL_RELEASE_FROM_JAIL = COSA_NOSTRA_API_URI + "/releaseFromJail/{id}";

    @Autowired
    private ICosaNostraManager cosaNostra;
    
    @Autowired
	CosaNostraFactory factory;

    @Before
    public void beforeCosaNostraControllerTest() {
    }

    @Test
    public void getOrganization() throws Exception {
        MockHttpServletResponse response = perform(defaultGet(URL_GET_ORGA));
        assertEquals(response.getErrorMessage(), 200, response.getStatus());
        String contentAsString = response.getContentAsString();
        assertTrue(StringUtils.isNotBlank(contentAsString));
        Map<String, Object> organization = new ObjectMapper().readValue(contentAsString, new TypeReference<HashMap<String, Object>>() {
        });
        assertNotNull(organization);
    }

    @Test
    public void getWatchList() throws Exception {
        MockHttpServletResponse response = perform(defaultGet(URL_GET_WATCH_LIST));
        assertEquals(response.getErrorMessage(), 200, response.getStatus());
        String contentAsString = response.getContentAsString();
        assertTrue(StringUtils.isNotBlank(contentAsString));
        List<Mafioso> organization = new ObjectMapper().readValue(contentAsString, new TypeReference<List<Mafioso>>() {});
        assertNotNull(organization);
        organization.forEach(new Consumer<Mafioso>() {
            @Override
            public void accept(Mafioso t) {
                IMafiosoManagerTest.assertMafioso(t);
            }
        });
    }

    @Test
    public void testJail() throws Exception {
        // Ok testing to send the cupula to jail...
        MafiaOrganization organization = cosaNostra.getOrganization();
        Mafioso oldCupula = organization.getCupula();
        String oldCupulaId = oldCupula.getId();
        MockHttpServletResponse response = perform(defaultPost(URL_SEND_TO_JAIL, oldCupulaId));
        assertEquals(response.getErrorMessage(), 200, response.getStatus());
        String contentAsString = response.getContentAsString();
        assertTrue(StringUtils.isBlank(contentAsString));

        Mafioso newCupula = organization.getCupula();
        String newCupulaId = newCupula.getId();
        assertFalse(StringUtils.equals(oldCupulaId, newCupulaId));

        response = perform(defaultPost(URL_RELEASE_FROM_JAIL, oldCupulaId));
        assertEquals(response.getErrorMessage(), 200, response.getStatus());
        contentAsString = response.getContentAsString();
        assertTrue(StringUtils.isBlank(contentAsString));

        Mafioso newestCupula = organization.getCupula();
        String newestCupulaId = newestCupula.getId();
        assertTrue(StringUtils.equals(oldCupulaId, newestCupulaId));
        IMafiosoManagerTest.assertMafiososEquals(newestCupula, oldCupula);
        
		// and then some random guy within the organization
        organization.erase();
        
        Mafioso cupula = factory.createGodfather(true);
        Mafioso newRecruit = factory.createRandomMafioso(true);
        Mafioso newestRecruit = factory.createRandomMafioso(true);
        organization.setCupula(cupula);
        organization.addSubordinate(cupula, newRecruit);
        organization.addSubordinate(newRecruit, newestRecruit);
        organization.addSubordinate(newRecruit, factory.createRandomMafioso(true));
        organization.addSubordinate(newRecruit, factory.createRandomMafioso(true));
        
        response = perform(defaultPost(URL_SEND_TO_JAIL, newRecruit.getId()));
        assertEquals(response.getErrorMessage(), 200, response.getStatus());
        contentAsString = response.getContentAsString();
        assertTrue(StringUtils.isBlank(contentAsString));
        
        
        response = perform(defaultPost(URL_RELEASE_FROM_JAIL, newRecruit.getId()));
        assertEquals(response.getErrorMessage(), 200, response.getStatus());
        contentAsString = response.getContentAsString();
        assertTrue(StringUtils.isBlank(contentAsString));
    }
}
