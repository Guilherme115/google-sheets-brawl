package brawl.example.project_brawl_api_sheets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository")

public class ProjectBrawlApiSheetsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectBrawlApiSheetsApplication.class, args);
	}

}
