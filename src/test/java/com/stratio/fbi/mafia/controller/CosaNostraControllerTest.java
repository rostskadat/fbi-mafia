package com.stratio.fbi.mafia.controller;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.fbi.mafia.AbstractControllerTest;
import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.MafiaOrganization;

public class CosaNostraControllerTest extends AbstractControllerTest {

    private static final String COSA_NOSTRA_API_URI = "/api/cosaNostra";
    private static final String URL_GET_ORGA = COSA_NOSTRA_API_URI + "/getOrganization";

    @Before
    public void beforeCosaNostraControllerTest() {
    }

    @Test
    public void getOrganization() throws Exception {
        MockHttpServletResponse response = perform(defaultGet(URL_GET_ORGA));
        assertEquals(response.getErrorMessage(), 200, response.getStatus());
        String contentAsString = response.getContentAsString();
        assertTrue(StringUtils.isNotBlank(contentAsString));
        MafiaOrganization organization = new ObjectMapper().readValue(contentAsString, MafiaOrganization.class);
        assertNotNull(organization);
    }

    public static Mafioso checkMafioso(Mafioso mafioso) {
        assertNotNull("Expected a non null mafioso", mafioso);
        assertNotNull("Expected a non-null mafioso.id", mafioso.getId());
        assertNotNull("Expected a non-null mafioso.firstName", mafioso.getFirstName());
        assertNotNull("Expected a non-null mafioso.lastName", mafioso.getLastName());
        assertNotNull("Expected a non-null mafioso.age", mafioso.getAge());
        return mafioso;
    }
}
