package org.mashupmedia.initialise;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@ComponentScans({ @ComponentScan("org.mashupmedia.service"), @ComponentScan("org.mashupmedia.dao"),
		@ComponentScan("org.mashupmedia.security"), @ComponentScan("org.mashupmedia.editor"),
		@ComponentScan("org.mashupmedia.interceptor"), @ComponentScan("org.mashupmedia.restful"),
		@ComponentScan("org.mashupmedia.validator"), @ComponentScan("org.mashupmedia.encode"),
		@ComponentScan("org.mashupmedia.initialise"), @ComponentScan("org.mashupmedia.task") })
@SpringBootApplication
public class MashupMediaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MashupMediaApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

		};
	}
}
