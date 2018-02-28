package net.pictulog.wfs.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cache.Cache;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.afb.arc.data.controller.JPADBModel;
import com.afb.arc.data.controller.JPADBRunner;
import com.afb.arc.data.controller.MockJPADBProxy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integration.properties")
public class MafiosoControllerTest {

    @Autowired
    private GenericApplicationContext appContext;
    
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    
    @Before
    public void beforeAbstractApiTest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }    
    @Test
    public void getMafiosoById() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString(),
                String.class);
        assertThat(response.getBody(), notNullValue());
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

    protected String getCallParameter(String methodName, String parameterName) {
        String className = getClass().getName();
        if (parameters != null && parameters.containsKey(className)) {
            Map<?, ?> classParameters = (Map<?, ?>) parameters.get(className);
            if (classParameters != null && classParameters.containsKey(methodName)) {
                Map<?, ?> methodParameters = (Map<?, ?>) classParameters.get(methodName);
                if (methodParameters != null && methodParameters.containsKey(parameterName)) {
                    return (String) methodParameters.get(parameterName);
                }
            } else if (classParameters != null && classParameters.containsKey(parameterName)) {
                return (String) classParameters.get(parameterName);
            }
        }
        throw new IllegalArgumentException("No such parameter for " + className + "." + methodName + " / " + parameterName);
    }
    
}