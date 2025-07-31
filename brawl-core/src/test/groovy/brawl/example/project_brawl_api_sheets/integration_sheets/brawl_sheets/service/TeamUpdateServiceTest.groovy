package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.ParticipantDTO
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.PlayerDTO
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerMODEL
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.Region
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamRegisterMODEL
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.PlayerRepository
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.TeamRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ResourceLoader
import spock.lang.Specification
import spock.lang.Subject

class TeamUpdateServiceTest extends Specification {

    // --- Dependências Mockadas ---
    def teamRegisterRepository = Mock(TeamRepository)
    def playerRepository = Mock(PlayerRepository)
    def objectMapper = Mock(ObjectMapper)
    def resourceLoader = Mock(ResourceLoader)

    // --- Classe Sob Teste ---
    @Subject
    def teamUpdateService = new TeamUpdateService(resourceLoader, objectMapper, teamRegisterRepository, playerRepository)

    def "deve criar um time novo e seus jogadores se eles não existirem"() {
        given: "Um DTO de um time completamente novo"
        def newPlayerDto = new PlayerDTO(name: "Novo Player", gamerTag: "#NEW_TAG")
        def newTeamDto = new ParticipantDTO(name: "Time Novo", players: [newPlayerDto])

        // Simula que o banco não encontrou nada
        teamRegisterRepository.findByName("Time Novo") >> Optional.empty()
        playerRepository.findById("NEW_TAG") >> Optional.empty()

        when: "Processamos o participante diretamente"
        teamUpdateService.processParticipant(newTeamDto, Region.SA)

        then: "O serviço salva o novo jogador e o novo time"
        1 * playerRepository.save({ PlayerMODEL player -> player.tag == "NEW_TAG" })
        1 * teamRegisterRepository.save({ TeamRegisterMODEL team -> team.name == "Time Novo" })
    }

    def "NÃO deve criar um time ou jogador que já existe"() {
        given: "Um DTO de um time e jogador que já estão no banco"
        def existingPlayerDto = new PlayerDTO(name: "Player Antigo", gamerTag: "#EXISTING_TAG")
        def existingTeamDto = new ParticipantDTO(name: "Time Antigo", players: [existingPlayerDto])

        // Simula que o banco ENCONTROU o time e o jogador
        teamRegisterRepository.findByName("Time Antigo") >> Optional.of(new TeamRegisterMODEL())
        playerRepository.findById("EXISTING_TAG") >> Optional.of(new PlayerMODEL())

        when: "Processamos o participante"
        teamUpdateService.processParticipant(existingTeamDto, Region.NA)

        then: "Nenhum método de 'save' é chamado"
        0 * playerRepository.save(_)
        0 * teamRegisterRepository.save(_)
    }
}