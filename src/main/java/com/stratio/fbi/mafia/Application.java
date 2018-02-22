package com.stratio.fbi.mafia;

import static java.lang.String.format;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@Controller
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(final ApplicationContext ctx) {
		return new CommandLineRunner() {

			@Override
			public void run(String... arg0) throws Exception {
				Log log = LogFactory.getLog(Application.class);
				log.info(format("Starting %s. Please standby...", Application.class.getName()));
				if (log.isDebugEnabled()) {
					log.debug("Let's inspect the beans provided by Spring Boot:");
					String[] beanNames = ctx.getBeanDefinitionNames();
					Arrays.sort(beanNames);
					for (String beanName : beanNames) {
						log.debug(beanName);
					}
				}
			}
		};
	}

	// Match everything without a suffix (so not a static resource)
	@RequestMapping(value = "/{path:[^\\.]*}")
	public String redirect() {
		// Forward to home page so that route is preserved.
		return "forward:/";
	}
}