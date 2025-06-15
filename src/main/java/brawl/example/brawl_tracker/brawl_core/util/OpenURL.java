package brawl.example.brawl_tracker.brawl_core.util;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class OpenURL {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
