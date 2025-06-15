package brawl.example.project_brawl_api_sheets.integration_sheets.util;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class OpenURL {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
