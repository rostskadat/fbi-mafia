package com.stratio.fbi.mafia.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.fbi.mafia.AbstractControllerTest;
import com.stratio.fbi.mafia.config.GlobalExceptionHandler.ApiError;
import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.jpa.MafiosoRepository;
import com.stratio.fbi.mafia.managers.ICosaNostraManager;
import com.stratio.fbi.mafia.model.Mafioso;

public class MafiosoControllerTest extends AbstractControllerTest {

	private static final String MAFIOSO_API_URI = "/api/mafioso";

	private static final String URL_ID = MAFIOSO_API_URI + "/{id}";
	private static final String URL_ADD = MAFIOSO_API_URI;
	private static final String URL_SUBORDINATES = MAFIOSO_API_URI + "/{id}/subordinates";
    private static final String URL_SEND_TO_JAIL = MAFIOSO_API_URI + "/{id}/sendToJail";
    private static final String URL_RELEASE_FROM_JAIL = MAFIOSO_API_URI + "/{id}/releaseFromJail";

	@Autowired
	private CosaNostraFactory factory;

    @Autowired
    private ICosaNostraManager cosaNostra;

	@Autowired
	private MafiosoRepository mafiosoRepository;

	private Mafioso alCapone;
	private Mafioso newRecruit;
	private Mafioso deletedRecruit;
	private Mafioso addedRecruit;

	@Before
	public void beforeMafiosoControllerTest() {
        // mafiosoRepository.deleteAllInBatch();
		alCapone = mafiosoRepository.save(factory.createGodfather());
		newRecruit = mafiosoRepository.save(factory.createRandomMafioso());
		deletedRecruit = mafiosoRepository.save(factory.createRandomMafioso());
		addedRecruit = factory.createRandomMafioso();
	}

	@Test
	public void mafiosoNotFound() throws Exception {
		MockHttpServletResponse response = perform(defaultGet(URL_ID, "INVALID_ID"));
		assertEquals(response.getErrorMessage(), 404, response.getStatus());
		String contentAsString = response.getContentAsString();
		assertTrue(StringUtils.isNotBlank(contentAsString));
		ApiError e = new ObjectMapper().readValue(contentAsString, ApiError.class);
		assertNotNull(e);
		assertNotNull(e.getField());
		assertNotNull(e.getMessage());
	}

	@Test
	public void getMafioso() throws Exception {
		MockHttpServletResponse response = perform(defaultGet(URL_ID, alCapone.getId()));
		assertEquals(response.getErrorMessage(), 200, response.getStatus());
		String contentAsString = response.getContentAsString();
		assertTrue(StringUtils.isNotBlank(contentAsString));
		checkMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
	}

	@Test
	public void updateMafioso() throws Exception {
		newRecruit.setFirstName("UPDATED_FIRST_NAME");
		MockHttpServletResponse response = perform(defaultPost(URL_ID, newRecruit.getId()).content(toJSONString(newRecruit)));
		assertEquals(response.getErrorMessage(), 200, response.getStatus());
		String contentAsString = response.getContentAsString();
		assertTrue(StringUtils.isNotBlank(contentAsString));
		Mafioso mafioso = checkMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
		assertTrue(StringUtils.equals(mafioso.getFirstName(), "UPDATED_FIRST_NAME"));
	}

	@Test
	public void deleteMafioso() throws Exception {
		MockHttpServletResponse response = perform(defaultDelete(URL_ID, deletedRecruit.getId()));
		assertEquals(response.getErrorMessage(), 200, response.getStatus());
		String contentAsString = response.getContentAsString();
		assertTrue(StringUtils.isNotBlank(contentAsString));
		Mafioso mafioso = checkMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
		assertEquals(mafioso.getFirstName(), deletedRecruit.getFirstName());
		assertEquals(mafioso.getLastName(), deletedRecruit.getLastName());
		assertEquals(mafioso.getAge(), deletedRecruit.getAge());
		response = perform(defaultGet(URL_ID, deletedRecruit.getId()));
		assertEquals(response.getErrorMessage(), 404, response.getStatus());
	}

	@Test
	public void addMafioso() throws Exception {
		MockHttpServletResponse response = perform(defaultPut(URL_ADD).content(toJSONString(addedRecruit)));
		assertEquals(response.getErrorMessage(), 200, response.getStatus());
		String contentAsString = response.getContentAsString();
		assertTrue(StringUtils.isNotBlank(contentAsString));
		Mafioso mafioso = checkMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
		assertEquals(mafioso.getFirstName(), addedRecruit.getFirstName());
		assertEquals(mafioso.getLastName(), addedRecruit.getLastName());
		assertEquals(mafioso.getAge(), addedRecruit.getAge());
		response = perform(defaultGet(URL_ID, mafioso.getId()));
		assertEquals(response.getErrorMessage(), 200, response.getStatus());
		contentAsString = response.getContentAsString();
		assertTrue(StringUtils.isNotBlank(contentAsString));
		mafioso = checkMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
		assertEquals(mafioso.getFirstName(), addedRecruit.getFirstName());
		assertEquals(mafioso.getLastName(), addedRecruit.getLastName());
		assertEquals(mafioso.getAge(), addedRecruit.getAge());
	}
	
	@Test
	public void getSubordinates() throws Exception {
        String cupulaId = cosaNostra.getOrganization().getCupula().getId();
        MockHttpServletResponse response = perform(defaultGet(URL_SUBORDINATES, cupulaId));
		assertEquals(response.getErrorMessage(), 200, response.getStatus());
		String contentAsString = response.getContentAsString();
		assertTrue(StringUtils.isNotBlank(contentAsString));
        List<Mafioso> mafiosos = new ObjectMapper().readValue(contentAsString, new TypeReference<List<Mafioso>>() {
        });
        assertNotNull(mafiosos);
        assertFalse(mafiosos.isEmpty());
        for (Mafioso mafioso : mafiosos) {
            checkMafioso(mafioso);
        }
	}

    @Test
    public void testJail() throws Exception {
        Mafioso oldCupula = cosaNostra.getOrganization().getCupula();
        String oldCupulaId = oldCupula.getId();
        MockHttpServletResponse response = perform(defaultPost(URL_SEND_TO_JAIL, oldCupulaId));
        assertEquals(response.getErrorMessage(), 200, response.getStatus());
        String contentAsString = response.getContentAsString();
        assertTrue(StringUtils.isBlank(contentAsString));

        Mafioso newCupula = cosaNostra.getOrganization().getCupula();
        String newCupulaId = newCupula.getId();
        assertFalse(StringUtils.equals(oldCupulaId, newCupulaId));

        response = perform(defaultPost(URL_RELEASE_FROM_JAIL, oldCupulaId));
        assertEquals(response.getErrorMessage(), 200, response.getStatus());
        contentAsString = response.getContentAsString();
        assertTrue(StringUtils.isNotBlank(contentAsString));
        Mafioso mafioso = checkMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
        assertEquals(oldCupula.getId(), mafioso.getId());
        assertEquals(oldCupula.getFirstName(), mafioso.getFirstName());
        assertEquals(oldCupula.getLastName(), mafioso.getLastName());
        assertEquals(oldCupula.getAge(), mafioso.getAge());

        Mafioso newestCupula = cosaNostra.getOrganization().getCupula();
        String newestCupulaId = newestCupula.getId();
        assertTrue(StringUtils.equals(oldCupulaId, newestCupulaId));
        assertEquals(newestCupula.getId(), mafioso.getId());
        assertEquals(newestCupula.getFirstName(), mafioso.getFirstName());
        assertEquals(newestCupula.getLastName(), mafioso.getLastName());
        assertEquals(newestCupula.getAge(), mafioso.getAge());

        // check that it is the boss
        response = perform(defaultPost(URL_RELEASE_FROM_JAIL, oldCupulaId));
        assertEquals(response.getErrorMessage(), 200, response.getStatus());
        contentAsString = response.getContentAsString();
        assertTrue(StringUtils.isNotBlank(contentAsString));

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
