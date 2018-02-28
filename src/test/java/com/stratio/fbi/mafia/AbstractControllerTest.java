package com.stratio.fbi.mafia;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ContextHierarchy({
        @ContextConfiguration(classes = { Config.class })
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:randomCosaNostra.properties")

// @WebAppConfiguration
// @TestExecutionListeners(listeners = { ServletTestExecutionListener.class,
// DependencyInjectionTestExecutionListener.class })
public abstract class AbstractControllerTest extends AbstractUnitTest {

    // @Autowired
    // protected GenericApplicationContext appContext;

    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;

    @Before
    public void beforeAbstractControllerTest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    protected MockHttpServletResponse perform(RequestBuilder requestBuilder) throws Exception {
        return this.mockMvc.perform(requestBuilder).andReturn().getResponse();
    }

    protected MockHttpServletRequestBuilder defaultGet(String path) {
        return get(path).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder defaultGet(String path, HttpHeaders httpHeaders) {
        return get(path).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders);
    }

    protected MockHttpServletRequestBuilder defaultGet(String path, Object... urlVariables) {
        return get(path, urlVariables).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder defaultPost(String path) {
        return post(path).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder defaultPost(String path, Object... urlVariables) {
        return post(path, urlVariables).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder defaultPut(String path) {
        return put(path).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder defaultPut(String path, Object... urlVariables) {
        return put(path, urlVariables).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder defaultDelete(String path) {
        return delete(path).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder defaultDelete(String path, Object... urlVariables) {
        return delete(path, urlVariables).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    }

    protected String toJSONString(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
