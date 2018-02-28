package com.stratio.fbi.mafia.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.fbi.mafia.AbstractControllerTest;
import com.stratio.fbi.mafia.config.GlobalExceptionHandler.ApiError;
import com.stratio.fbi.mafia.demo.CosaNostraFactory;
import com.stratio.fbi.mafia.jpa.MafiosoRepository;
import com.stratio.fbi.mafia.model.Mafioso;

public class MafiosoControllerTest extends AbstractControllerTest {

	private static final String MAFIOSO_API_URI = "/api/mafioso";

	private static final String URL_ID = MAFIOSO_API_URI + "/{id}";
	private static final String URL_ADD = MAFIOSO_API_URI;

	@Autowired
	private CosaNostraFactory factory;

	// @MockBean
	// private MafiosoController service;

	@Autowired
	private MafiosoRepository mafiosoRepository;

	private List<Mafioso> mafiosos = new ArrayList<>();

	private Mafioso alCapone;
	private Mafioso newRecruit;

	@Before
	public void beforeMafiosoControllerTest() {
		mafiosoRepository.deleteAllInBatch();
		alCapone = mafiosoRepository.save(factory.createGodfather());
		newRecruit = mafiosoRepository.save(factory.createRandomMafioso());
		mafiosos.addAll(Arrays.asList(new Mafioso[] { alCapone, newRecruit }));

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
		MockHttpServletResponse response = perform(defaultPost(URL_ID, newRecruit.getId()));
		assertEquals(response.getErrorMessage(), 200, response.getStatus());
		String contentAsString = response.getContentAsString();
		assertTrue(StringUtils.isNotBlank(contentAsString));
		Mafioso mafioso = checkMafioso(new ObjectMapper().readValue(contentAsString, Mafioso.class));
		assertTrue(StringUtils.equals(mafioso.getFirstName(), "UPDATED_FIRST_NAME"));
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
