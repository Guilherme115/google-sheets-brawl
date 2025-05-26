package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.model.BrawlRequestMODEL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class BrawlService {
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    @Value("${brawl.api.key}")
    private String apiToken;

    List<String> modos3x3 = Arrays.asList(
            "brawlBall", "gemGrab", "bounty", "heist",
            "hotZone", "knockout", "siege", "wipeout"
    );

@Autowired
    public BrawlService(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    public Map<String, List<BrawlRequestMODEL.BattleLogInfo>> getTeams(String mainTag, HashMap<List<String>, String> tags) {

        Map<String, List<BrawlRequestMODEL.BattleLogInfo>> objectReturn = new HashMap<>();

        Optional<List<String>> listaDoTime = tags.keySet().stream()
                .filter(lista -> lista.contains(mainTag))
                .findFirst();

        if (listaDoTime.isPresent()) {
            List<String> listaTags = listaDoTime.get();

            String nomeEquipe = tags.get(listaTags);

            List<BrawlRequestMODEL.BattleLogInfo> partidas = getFilteredBattleLogs(mainTag, is3v3AndFriendlyMatchAndContainsAllPlayer(listaTags));

            objectReturn.put(nomeEquipe, partidas);
        } else {
            log.error("Não foi possível encontrar a equipe para a tag principal '{}'. Verifique a lógica de negócio.", mainTag);
        }

        return objectReturn;
    }

    private List<BrawlRequestMODEL.BattleLogInfo> getFilteredBattleLogs(String playerTag, Predicate<BrawlRequestMODEL.BattleLogInfo>... filters) {

        String rawJson = fetchRawJson(playerTag);
        BrawlRequestMODEL model = parseJson(rawJson);

        if (model == null || model.getItems() == null) {
            log.warn("Modelo nulo ou sem items");
            return Collections.emptyList();
        }

        Predicate<BrawlRequestMODEL.BattleLogInfo> combined = Arrays.stream(filters)
                .reduce(x -> true, Predicate::and);

        return model.getItems().stream()
                .filter(combined)
                .collect(Collectors.toList());
    }

    private ResponseEntity<String> fetchBattleLogFromApi(String mainTag) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "https://api.brawlstars.com/v1/players/%23" + mainTag + "/battlelog";
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

    private Predicate<BrawlRequestMODEL.BattleLogInfo> is3v3Match() {
        return item -> item != null &&
                item.getBattle() != null &&
                modos3x3.contains(item.getBattle().getType());
    }

    private Predicate<BrawlRequestMODEL.BattleLogInfo> isFriendlyMatch() {
        return item -> item != null &&
                item.getBattle() != null &&
                "friendly".equalsIgnoreCase(item.getBattle().getType());
    }

    private Predicate<BrawlRequestMODEL.BattleLogInfo> is3v3AndFriendlyMatchAndContainsAllPlayer(List<String> tags) {
        return is3v3Match().and(isFriendlyMatch().and(isTeamPresent(tags)));
    }

    private Predicate<BrawlRequestMODEL.BattleLogInfo> isTeamPresent(List<String> tags) {
        return player -> {
            List<String> playerTags = player.getBattle()
                    .getTeams()
                    .getPlayersList()
                    .stream()
                    .map(p -> p.getTag().replace("#", ""))
                    .collect(Collectors.toList());

            return playerTags.containsAll(tags);
        };
    }
}
