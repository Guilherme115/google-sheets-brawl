package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.PlayerValidDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    private PlayerValidDTO parseJson(String json) {
        try {
            PlayerValidDTO model = mapper.readValue(json, PlayerValidDTO.class);
            log.info("JSON desserializado com sucesso. Quantidade de items: {}");
            if (model.getName() != null) return model;

        } catch (JsonProcessingException e) {
            log.error("Erro ao desserializar JSON", e);
            throw new RuntimeException("Erro ao desserializar JSON", e);
        }
        return null;
    }
    public boolean isPlayerTagisValid(String tag) {
        try {
            // Nós chamamos o método que faz a requisição diretamente aqui.
            // Se a API retornar 404, a linha abaixo vai lançar a exceção.
            fetchBattleLogFromApi(tag);

            // Se a linha acima NÃO lançar uma exceção, significa que a API retornou
            // um status de sucesso (2xx). Portanto, a tag existe e é válida.
            return true;

        } catch (HttpClientErrorException.NotFound e) {
            // Se a exceção for especificamente "404 Not Found",
            // significa que a tag não existe. Para nossa lógica, isso é uma
            // validação bem-sucedida de que a tag é INVÁLIDA.
            log.warn("Tag '{}' não encontrada na API do Brawl Stars (404). Considerada inválida.", tag);
            return false; // A tag é inválida porque não foi encontrada.

        } catch (Exception e) {
            // Para qualquer outro erro (403 Forbidden, 500, etc.),
            // logamos o erro e também consideramos a validação como falha.
            log.error("Erro inesperado ao validar a tag '{}'. Detalhes: {}", tag, e.getMessage());
            return false;
        }
    }

    public String NameOfPlayer (String tag) {

        String raw = fetchRawJson(tag);

        try {
            PlayerValidDTO player = parseJson(raw);
            String name = player.getName();
            return name;

        } catch (Exception e) {
            return null;
        }
    }

}
