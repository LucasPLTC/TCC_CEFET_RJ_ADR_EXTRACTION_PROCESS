package edu.cefet.tcc.load;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoadApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoadApplication.class, args);
	}

}
