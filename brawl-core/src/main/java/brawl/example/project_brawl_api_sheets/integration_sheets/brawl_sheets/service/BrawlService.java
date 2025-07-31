package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO;
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
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BrawlService {

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    @Value("${brawl.api.key}")
    private String apiToken;

    final List<String> modos3x3 = Arrays.asList(
            "brawlBall", "gemGrab", "bounty", "heist",
            "hotZone", "knockout", "siege", "wipeout"
    );

    public BrawlService(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    /**
     * Busca o log de batalhas completo de um jogador, sem aplicar nenhum filtro.
     * Apenas busca e converte o JSON.
     */
    public BattleLogReceiveDTO fetchPlayerBattleLog(String mainTag) {
        String rawJson = fetchRawJson(mainTag);
        if (rawJson == null) {
            log.warn("Não foi possível obter o JSON para a tag {}", mainTag);
            return null;
        }
        return parseJson(rawJson);
    }

    /**
     * NOVO MÉTODO PRINCIPAL DE FILTRAGEM:
     * Recebe um log de batalha completo e aplica TODOS os filtros necessários
     * (3x3, tipo de partida e presença do time).
     */
    public List<BattleLogReceiveDTO.BattleLogInfo> getFilteredBattles(BattleLogReceiveDTO battleLog, List<String> teamTags) {
        if (battleLog == null || battleLog.getItems() == null) {
            return Collections.emptyList();
        }

        // Combina todos os filtros em um só
        Predicate<BattleLogReceiveDTO.BattleLogInfo> allFilters = is3v3Match()
                .and(isAllowedMatchType())
                .and(isTeamPresent(teamTags));

        List<BattleLogReceiveDTO.BattleLogInfo> filteredList = battleLog.getItems().stream()
                .filter(allFilters)
                .collect(Collectors.toList());

        log.info("De {} batalhas recebidas, {} passaram em todos os filtros.", battleLog.getItems().size(), filteredList.size());
        return filteredList;
    }

    // Métodos privados continuam os mesmos...

    private String fetchRawJson(String mainTag) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = "https://api.brawlstars.com/v1/players/%23" + mainTag.replace("#", "") + "/battlelog";
            URI uri = URI.create(url);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Erro HTTP ao chamar API do Brawl para a tag {}: {} - {}", mainTag, e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Falha genérica na chamada da API do Brawl para a tag {}", mainTag, e);
            return null;
        }
    }

    private BattleLogReceiveDTO parseJson(String json) {
        if (json == null) return null;
        try {
            BattleLogReceiveDTO model = mapper.readValue(json, BattleLogReceiveDTO.class);
            log.info("JSON desserializado com sucesso. Quantidade de items recebidos da API: {}",
                    model.getItems() != null ? model.getItems().size() : 0);
            return model;
        } catch (JsonProcessingException e) {
            log.error("Erro ao desserializar JSON", e);
            return null;
        }
    }

    private Predicate<BattleLogReceiveDTO.BattleLogInfo> is3v3Match() {
        return item -> item != null &&
                item.getBattle() != null &&
                item.getBattle().getMode() != null &&
                modos3x3.contains(item.getBattle().getMode());
    }

    private Predicate<BattleLogReceiveDTO.BattleLogInfo> isAllowedMatchType() {
        final Set<String> allowedTypes = Set.of("friendly", "tournament");
        return item -> {
            if (item == null || item.getBattle() == null || item.getBattle().getType() == null) {
                return false;
            }
            return allowedTypes.contains(item.getBattle().getType());
        };
    }

    private Predicate<BattleLogReceiveDTO.BattleLogInfo> isTeamPresent(List<String> registeredTeamTags) {
        final Set<String> ourTeamTags = registeredTeamTags.stream()
                .map(tag -> tag.replace("#", ""))
                .collect(Collectors.toSet());

        return battleLogInfo -> {
            if (battleLogInfo == null || battleLogInfo.getBattle() == null || battleLogInfo.getBattle().getTeams() == null) {
                return false;
            }
            List<List<BattleLogReceiveDTO.Player>> teamsInBattle = battleLogInfo.getBattle().getTeams();
            for (List<BattleLogReceiveDTO.Player> teamInBattle : teamsInBattle) {
                if (teamInBattle == null) continue;

                long matchingPlayers = teamInBattle.stream()
                        .filter(Objects::nonNull)
                        .map(player -> player.getTag().replace("#", ""))
                        .filter(ourTeamTags::contains)
                        .count();
                if (matchingPlayers >= 2) {
                    return true;
                }
            }
            return false;
        };
    }
}