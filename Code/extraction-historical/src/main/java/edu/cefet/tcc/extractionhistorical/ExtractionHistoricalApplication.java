package edu.cefet.tcc.extractionhistorical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ExtractionHistoricalApplication {
	public static void main(String[] args) {
		SpringApplication.run(ExtractionHistoricalApplication.class, args);
	}
}
