package brawl.example.brawl_tracker.service;

import brawl.example.brawl_tracker.dto.TeamBattleDTO;
import brawl.example.brawl_tracker.model.BrawlRequestMODEL;
import brawl.example.brawl_tracker.model.TeamMODEL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class BrawlService {
    /*
    A bilioteca ObjectMapper vai servir para converter objeto em json, e json em objeto.
     */
    private final ObjectMapper mapper;
    /*
    - Alimentar uma api, tipo colocar uma url e pegar os dados em uma classe mdoel.
    - Principal forma de abrir a conexao com a api.
     */
    private final RestTemplate restTemplate;



    /*
    chave key em resouces
     */

    @Value("${brawl.api.key}")
    private String apiToken;
    /*
     - É necessario verifcar se os eventos procedem como eventos competitivos de brawl stars
     */

    List<String> modos3x3 = Arrays.asList(
            "brawlBall", "gemGrab", "bounty", "heist",
            "hotZone", "knockout", "siege", "wipeout"
    );

    @Autowired
    public BrawlService(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    /*
   Vou otimizar
     -
     */

    public TeamBattleDTO getTeams(String mainTag, TeamMODEL model) {
        if (model == null) {
            log.warn("Model é nulo. Não é possível continuar.");
            return null;
        }

        List<String> listaTags = model.getPlayersTags();
        if (listaTags == null || listaTags.isEmpty() || listaTags.stream().allMatch(String::isBlank)) {
            log.warn("Lista de tags é vazia ou inválida.");
            return null;
        }

        if (listaTags.contains(mainTag)) {
            String nomeEquipe = model.getTeamName();
            List<BrawlRequestMODEL.BattleLogInfo> partidas = getFilteredBattleLogs(
                    mainTag,
                    is3v3AndFriendlyMatchAndContainsAllPlayer(listaTags)
            );
            return new TeamBattleDTO(nomeEquipe, partidas);
        } else {
            log.error("Não foi possível encontrar a equipe para a tag principal '{}'. Verifique a lógica de negócio.", mainTag);
            return null;
        }
    }


    /*
    Aqui vamos filtrar as batalhas com base no
     */

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
                modos3x3.contains(item.getBattle().getMode());
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
        return battleLogInfo -> {
            List<List<BrawlRequestMODEL.Player>> teams = battleLogInfo.getBattle().getTeams();

            for (List<BrawlRequestMODEL.Player> team : teams) {
                List<String> playerTags = team.stream()
                        .map(p -> p.getTag().replace("#", ""))
                        .collect(Collectors.toList());

                if (playerTags.containsAll(tags)) {
                    return true;
                }
            }

            return false;
        };
    }

}

