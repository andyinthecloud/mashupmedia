package org.mashupmedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MashupMediaApplication {

	// @Override
	// protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	// 	return application.sources(MashupMediaApplication.class);
	// }

	public static void main(String[] args) {
		SpringApplication.run(MashupMediaApplication.class, args);
	}

}
