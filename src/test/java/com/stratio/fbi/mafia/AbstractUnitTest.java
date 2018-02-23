package com.stratio.fbi.mafia;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(classes = { Config.class })
})
@TestExecutionListeners(listeners = { MyDependencyInjectionTestExecutionListener.class })
public abstract class AbstractUnitTest extends TestCase {

}
