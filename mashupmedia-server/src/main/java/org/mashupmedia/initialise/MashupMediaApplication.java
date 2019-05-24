package org.mashupmedia.initialise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({ "org.mashupmedia.**" })

@SpringBootApplication
public class MashupMediaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MashupMediaApplication.class, args);
	}

}
