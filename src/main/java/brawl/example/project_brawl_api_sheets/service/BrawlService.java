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

    private ResponseEntity<String> fetchBattleLogFromApi(String mainTag) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "https://api.brawlstars.com/v1/players/%23" + mainTag + "/battlelog";
        URI uri = URI.create(url);

        return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    public Stream<BrawlRequestMODEL.BattleLogInfo> getTeams(List<String> tags, List<BrawlRequestMODEL.BattleLogInfo> items) {
        Map<String, BrawlRequestMODEL.BattleLogInfo> partidaMap = new HashMap<>();
        Map<String, Set<String>> jogadoresPorPartida = new HashMap<>();

        for (String tag : tags) {
            List<BrawlRequestMODEL.BattleLogInfo> partidas = getFilteredBattleLogs(tag, is3v3AndFriendlyMatch());

            for (BrawlRequestMODEL.BattleLogInfo partida : partidas) {
                String idPartida = partida.getBattleTime() + "-" + partida.getBattle().getId();

                List<BrawlRequestMODEL.Player> jogadores = partida.getBattle()
                        .getTeams()
                        .getPlayersList();

                boolean jogadorParticipou = jogadores.stream()
                        .anyMatch(j -> tags.contains(j.getTag().replace("#", "")));

                if (jogadorParticipou) {
                    jogadoresPorPartida.computeIfAbsent(idPartida, k -> new HashSet<>()).add(tag);
                    partidaMap.putIfAbsent(idPartida, partida);
                }
            }
        }

        return jogadoresPorPartida.entrySet().stream()
                .filter(entry -> {
                    int count = entry.getValue().size();
                    return count >= 2 && count <= 4;
                })
                .map(entry -> partidaMap.get(entry.getKey()))
                .collect(Collectors.toList())
                .stream();
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

    private List<BrawlRequestMODEL.BattleLogInfo> getFilteredBattleLogs(
            String playerTag,
            Predicate<BrawlRequestMODEL.BattleLogInfo>... filters) {

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

    public Predicate<BrawlRequestMODEL.BattleLogInfo> is3v3Match() {
        return item -> item != null &&
                item.getBattle() != null &&
                modos3x3.contains(item.getBattle().getType());
    }

    public Predicate<BrawlRequestMODEL.BattleLogInfo> isFriendlyMatch() {
        return item -> item != null &&
                item.getBattle() != null &&
                "friendly".equalsIgnoreCase(item.getBattle().getType());
    }

    public Predicate<BrawlRequestMODEL.BattleLogInfo> is3v3AndFriendlyMatch() {
        return is3v3Match().and(isFriendlyMatch());
    }
}
