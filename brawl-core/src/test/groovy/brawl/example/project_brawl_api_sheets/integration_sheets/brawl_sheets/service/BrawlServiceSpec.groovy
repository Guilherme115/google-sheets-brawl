package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Subject

import java.nio.charset.StandardCharsets

class BrawlServiceSpec extends Specification {

    // --- Dependências ---
    def restTemplate = Mock(RestTemplate)
    def objectMapper = new ObjectMapper()

    // --- Classe Sob Teste ---
    @Subject
    def brawlService = new BrawlService(restTemplate, objectMapper)

    def "fetchPlayerBattleLog deve buscar e desserializar o JSON corretamente"() {
        given: "Um JSON de mock no classpath"
        def fakeJson = loadJsonFromResources("mock/BrawlLogMock.json")
        1 * restTemplate.exchange(_, HttpMethod.GET, _ as HttpEntity, String) >>
                new ResponseEntity<String>(fakeJson, HttpStatus.OK)

        when: "O método de busca do serviço é chamado"
        def result = brawlService.fetchPlayerBattleLog("QUALQUER_TAG")

        then: "O DTO é retornado com todos os 7 items do arquivo, sem filtros"
        result != null
        result.items.size() == 7
    }

    def "getFilteredBattles deve aplicar todos os filtros e retornar apenas os scrims válidos"() {
        given: "Um DTO completo e uma lista de tags do nosso time"
        def teamTags = ["#TEAM_TAG_1", "#TEAM_TAG_2", "#TEAM_TAG_3"]
        def fakeJson = loadJsonFromResources("mock/BrawlLogMock.json")
        def fullBattleLog = objectMapper.readValue(fakeJson, BattleLogReceiveDTO)

        when: "Chamamos o método de filtragem"
        def result = brawlService.getFilteredBattles(fullBattleLog, teamTags)

        then: "Apenas as 2 partidas válidas do mock são retornadas"
        result.size() == 2

        and: "As partidas retornadas são as corretas"
        def battleTimes = result.collect { it.battleTime }
        battleTimes.containsAll(["T1_VALID_FRIENDLY", "T2_VALID_TOURNAMENT"])
    }

    private String loadJsonFromResources(String fileName) {
        // Agora que o arquivo está no lugar certo, este método vai funcionar
        def inputStream = getClass().getClassLoader().getResourceAsStream(fileName)
        if (inputStream == null) {
            throw new FileNotFoundException("Arquivo de resource não encontrado: " + fileName)
        }
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
    }
}