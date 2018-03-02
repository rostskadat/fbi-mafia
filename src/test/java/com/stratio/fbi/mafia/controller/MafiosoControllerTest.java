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
import com.stratio.fbi.mafia.managers.IMafiosoManagerTest;
import com.stratio.fbi.mafia.model.Mafioso;

public class MafiosoControllerTest extends AbstractControllerTest {

	private static final String MAFIOSO_API_URI = "/api/mafioso";

	private static final String URL_ID = MAFIOSO_API_URI + "/{id}";
	private static final String URL_ADD = MAFIOSO_API_URI;
	private static final String URL_SUBORDINATES = MAFIOSO_API_URI + "/{id}/subordinates";

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
		IMafiosoManagerTest.assertMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
	}

	@Test
	public void updateMafioso() throws Exception {
		newRecruit.setFirstName("UPDATED_FIRST_NAME");
		MockHttpServletResponse response = perform(defaultPost(URL_ID, newRecruit.getId()).content(toJSONString(newRecruit)));
		assertEquals(response.getErrorMessage(), 200, response.getStatus());
		String contentAsString = response.getContentAsString();
		assertTrue(StringUtils.isNotBlank(contentAsString));
		Mafioso mafioso = IMafiosoManagerTest.assertMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
		assertTrue(StringUtils.equals(mafioso.getFirstName(), "UPDATED_FIRST_NAME"));
	}

	@Test
	public void deleteMafioso() throws Exception {
		MockHttpServletResponse response = perform(defaultDelete(URL_ID, deletedRecruit.getId()));
		assertEquals(response.getErrorMessage(), 200, response.getStatus());
		String contentAsString = response.getContentAsString();
		assertTrue(StringUtils.isNotBlank(contentAsString));
		Mafioso mafioso = IMafiosoManagerTest.assertMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
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
		Mafioso mafioso = IMafiosoManagerTest.assertMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
		assertEquals(mafioso.getFirstName(), addedRecruit.getFirstName());
		assertEquals(mafioso.getLastName(), addedRecruit.getLastName());
		assertEquals(mafioso.getAge(), addedRecruit.getAge());
		response = perform(defaultGet(URL_ID, mafioso.getId()));
		assertEquals(response.getErrorMessage(), 200, response.getStatus());
		contentAsString = response.getContentAsString();
		assertTrue(StringUtils.isNotBlank(contentAsString));
		mafioso = IMafiosoManagerTest.assertMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
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
            IMafiosoManagerTest.assertMafioso(mafioso);
        }
	}
}
