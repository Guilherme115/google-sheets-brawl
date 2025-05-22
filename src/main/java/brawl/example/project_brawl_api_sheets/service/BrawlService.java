package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.model.BrawlRequestMODEL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;
@Slf4j
@Service
public class BrawlService {
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    @Value("${brawl.api.key}")
    private String apiToken;

    @Autowired
    public BrawlService(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    private ResponseEntity<String> fetchBattleLogFromApi(String mainTag) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "https://api.brawlstars.com/v1/players/%23" + mainTag + "/battlelog";
        URI uri = URI.create(url);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        return response;
    }

    private String fetchRawJson(String mainTag) {
        ResponseEntity<String> response = fetchBattleLogFromApi(mainTag);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Erro ao chamar API do Brawl: status {}", response.getStatusCode());
            throw new RuntimeException("Erro ao buscar dados do Brawl API");
        }

        return response.getBody();
    }
    private BrawlRequestMODEL parseJson(String json) {
        try {
            BrawlRequestMODEL model = mapper.readValue(json, BrawlRequestMODEL.class);
            log.info("JSON desserializado com sucesso. Quantidade de items: {}",
                    model.getItems() != null ? model.getItems().size() : 0);
            return model;
        } catch (JsonProcessingException e) {
            log.error("Erro ao desserializar JSON", e);
            throw new RuntimeException("Erro ao desserializar JSON", e);
        }
    }
    private List<BrawlRequestMODEL.BattleLogInfo> filter3x3(List<BrawlRequestMODEL.BattleLogInfo> items) {
        List<String> modos3x3 = Arrays.asList(
                "brawlBall", "gemGrab", "bounty", "heist",
                "hotZone", "knockout", "siege", "wipeout"
        );

        return items.stream()
                .filter(item -> {
                    if (item == null || item.getBattle() == null) {
                        log.warn("Item nulo ou sem informação de batalha");
                        return false;
                    }
                    boolean matches = modos3x3.contains(item.getBattle().getType());
                    if (matches) {
                        log.info("Match encontrado para modo: {}", item.getBattle().getType());
                    }
                    return matches;
                })
                .collect(Collectors.toList());
    }



    private List<BrawlRequestMODEL.BattleLogInfo> get3v3BattleLogs(String playerTag) {
        String rawJson = fetchRawJson(playerTag);
        BrawlRequestMODEL model = parseJson(rawJson);

        if (model == null || model.getItems() == null) {
            log.warn("Modelo nulo ou sem items");
            return Collections.emptyList();
        }

        return filter3x3(model.getItems());
    }


    private List<BrawlRequestMODEL.BattleLogInfo> filterByBattleLogType(List<BrawlRequestMODEL.BattleLogInfo> items) {
        return items.stream()
                .filter(item -> {
                    if (item == null || item.getBattle() == null) {
                        log.warn("Item nulo ou sem informação de batalha");
                        return false;
                    }
                    boolean matches = "friendly".contains(item.getBattle().getType());
                    if (matches) {
                        log.info("Match encontrado para modo: {}", item.getBattle().getType());
                    }
                    return matches;
                })
                .collect(Collectors.toList());
    }

    private List<BrawlRequestMODEL.BattleLogInfo> getMatchesFriendely(String playerTag) {
        String rawJson = fetchRawJson(playerTag);
        BrawlRequestMODEL model = parseJson(rawJson);

        if (model == null || model.getItems() == null) {
            log.warn("Modelo nulo ou sem items");
            return Collections.emptyList();
        }

        return filterByBattleLogType(model.getItems());
    }



    }










