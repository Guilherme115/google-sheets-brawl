package brawl.example.brawl_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProjectBrawlApiSheetsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectBrawlApiSheetsApplication.class, args);
	}

}
