package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.model.PlayerValidModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.net.URI;

@Slf4j
@Service
public class PlayerTagService {

    private final ObjectMapper mapper;

    private final RestTemplate restTemplate;

    @Value("${brawl.api.key}")
    private String apiToken;

    public PlayerTagService(ObjectMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    private ResponseEntity<String> fetchBattleLogFromApi(String mainTag) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "https://api.brawlstars.com/v1/players/%23" + mainTag ;
        URI uri = URI.create(url);

        return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    private String fetchRawJson(String mainTag) {
        ResponseEntity<String> response = fetchBattleLogFromApi(mainTag);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Erro ao chamar API do Brawl: status {}", response.getStatusCode());
            throw new RuntimeException("Erro ao buscar dados do Brawl API");
        }

        return response.getBody();
    }

    private PlayerValidModel parseJson(String json) {
        try {
            PlayerValidModel model = mapper.readValue(json, PlayerValidModel.class);
            log.info("JSON desserializado com sucesso. Quantidade de items: {}");
            if (model.getName() != null) return model;

        } catch (JsonProcessingException e) {
            log.error("Erro ao desserializar JSON", e);
            throw new RuntimeException("Erro ao desserializar JSON", e);
        }
        return null;
    }
    public boolean isPlayerTagisValid(String tag) {
        String raw = fetchRawJson(tag);

        try {
            PlayerValidModel player = parseJson(raw);

            if (player.getTrophies() > 30000 || player.isQualifiedFromChampionshipChallenge() || NameOfPlayer(tag) != null) {
                return true;

            }
            return false;

        } catch (Exception e) {
            log.warn("Falha ao validar tag: {}", tag, e);
            return false;
        }
    }

    public String NameOfPlayer (String tag) {

        String raw = fetchRawJson(tag);

        try {
            PlayerValidModel player = parseJson(raw);
            String name = player.getName();
            return name;

        } catch (Exception e) {
            return null;
        }
    }

}
