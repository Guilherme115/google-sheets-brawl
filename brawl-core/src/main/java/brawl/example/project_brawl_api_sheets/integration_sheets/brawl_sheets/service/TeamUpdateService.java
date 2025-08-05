package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.*;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.*;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!test")
public class TeamUpdateService {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final TeamRepository teamRegisterRepository;
    private final PlayerRepository playerRepository;

    private static final Map<Region, String> REGION_FILES = Map.of(
            Region.SA, "data/SAMFs.json",
            Region.NA, "data/NAMFs.json",
            Region.EU, "data/EMEA MFs.json",
            Region.EA, "data/EAMFs.json"
    );

    @Scheduled(fixedRate = 86400000)
    @Transactional
    public void updateTeamsFromSource() { // <-- Nome do método mudou
        log.info("------------------- INICIANDO ROTINA DE ATUALIZAÇÃO DE TIMES -------------------");

        for (Map.Entry<Region, String> entry : REGION_FILES.entrySet()) {
            Region region = entry.getKey();
            String filePath = entry.getValue();
            log.info("Verificando times da região: {}", region.getDisplayName());

            try {
                Resource resource = resourceLoader.getResource("classpath:" + filePath);
                InputStream inputStream = resource.getInputStream();
                TournamentDataDTO tournamentData = objectMapper.readValue(inputStream, TournamentDataDTO.class);

                if (tournamentData != null && tournamentData.getParticipants() != null) {
                    for (ParticipantDTO participant : tournamentData.getParticipants()) {
                        // A lógica de processamento agora garante que apenas times novos sejam adicionados
                        processParticipant(participant, region);
                    }
                }
            } catch (Exception e) {
                log.warn("Falha ao carregar o arquivo de dados para a região {}: {} - {}", region, filePath, e.getMessage());
            }
        }
        log.info("------------------- ROTINA DE ATUALIZAÇÃO DE TIMES CONCLUÍDA -------------------");
    }

    // Este método agora é inteligente: ele cria se não existir, ou ignora se já existir.
     void processParticipant(ParticipantDTO participantDTO, Region region) {
        teamRegisterRepository.findByName(participantDTO.getName())
                .ifPresentOrElse(
                        (existingTeam) -> {
                            log.trace("Time '{}' já existe no banco de dados. Pulando.", existingTeam.getName());
                        },
                        () -> {
                            log.info("Novo time competitivo descoberto: {}", participantDTO.getName());
                            TeamRegisterMODEL newTeam = new TeamRegisterMODEL();
                            newTeam.setName(participantDTO.getName());
                            newTeam.setLogoUrl(participantDTO.getLogoUrl());
                            newTeam.setRegion(region);
                            newTeam.setCategory(TeamCategory.PROFESSIONAL);

                            for (PlayerDTO playerDTO : participantDTO.getPlayers()) {
                                if (playerDTO.getGamerTag() != null && !playerDTO.getGamerTag().isBlank()) {
                                    PlayerMODEL player = findOrCreatePlayer(playerDTO);
                                    newTeam.getPlayers().add(player);
                                }
                            }
                            teamRegisterRepository.save(newTeam);
                        }
                );
    }

     PlayerMODEL findOrCreatePlayer(PlayerDTO playerDTO) {
        String cleanedTag = playerDTO.getGamerTag().replace("#", "");
        return playerRepository.findById(cleanedTag)
                .orElseGet(() -> {
                    log.info("Novo jogador profissional descoberto: {} ({})", playerDTO.getName(), cleanedTag);
                    PlayerMODEL newPlayer = new PlayerMODEL();
                    newPlayer.setTag(cleanedTag);
                    newPlayer.setName(playerDTO.getName());
                    return playerRepository.save(newPlayer);
                });
    }
}