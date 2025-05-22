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

/*
1-Filter by Mode
2- Filter by Type
3 - Filter by Tags (Very Hard)
 */

    //Filter by Map
    public List<BrawlRequestMODEL.BattleLogInfo> FilterFor3X3(String mainTag) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization ", "Bearer " + apiToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            URI uri = URI.create("https://api.brawlstars.com/v1/players/%232CPVUVCCP/battleloghttps://api.brawlstars.com/v1/players/%23" + mainTag + "/battlelog");
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                System.err.println("Erro na requisição: " + response.getStatusCode());
                return Collections.emptyList();
            }

            String json = response.getBody();
            log.info("JSON recebido:\n{}", json);

            log.info("DEBUG: Tentando desserializar o JSON...");
            BrawlRequestMODEL requestMODEL = mapper.readValue(json, BrawlRequestMODEL.class);

            if (requestMODEL == null) {
                log.info("OBJETO requestMODEL está vazio verfique as credencias da API ou LOGICA de NEGOCIO");
                return Collections.emptyList();
            }

            log.info("requestModel desserializado com sucesso");


            List<String> modos3x3 = Arrays.asList(
                    "brawlBall",
                    "gemGrab",
                    "bounty",
                    "heist",
                    "hotZone",
                    "knockout",
                    "siege",
                    "wipeout"
            );


            requestMODEL.getItems().stream()
                    .filter(item-> {
                        if (item == null) {
                            log.info("(Filtro):NADA ENCONTRADO!");
                            return false;
                        }
                        BrawlRequestMODEL.Battle battle = item.getBattle();
                        if (battle == null) {
                            log.warn("NÃO ACHOU INFORMAÇÃO RELACIONADO A BATTLE");
                            return false;
                        }
                        for ()
                    });



return  null;    } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

