package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.TeamWithPlayersRelationDTO;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.*;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BrawlDataService {

    private final PlayerRepository playerRepository;
    private final BrawlerRepository brawlerRepository;
    private final BattleMatchRepository battleMatchRepository;
    private final TeamRepository teamRepository;
    private final PlayerPerfomanceRepository playerPerformanceRepository;


    public BrawlDataService(
            PlayerRepository playerRepository,
            BrawlerRepository brawlerRepository,
            BattleMatchRepository battleMatchRepository,
            TeamRepository teamRepository,
            PlayerPerfomanceRepository playerPerformanceRepository) {
        this.playerRepository = playerRepository;
        this.brawlerRepository = brawlerRepository;
        this.battleMatchRepository = battleMatchRepository;
        this.teamRepository = teamRepository;
        this.playerPerformanceRepository = playerPerformanceRepository;
    }

    @Transactional
    public void processAndSaveBattleLog(BattleLogReceiveDTO battleLogDTO, TeamWithPlayersRelationDTO teamInfo) {
        if (battleLogDTO == null || battleLogDTO.getItems() == null || battleLogDTO.getItems().isEmpty()) {
            log.warn("Nenhum log de batalha para processar.");
            return;
        }

        log.info("Iniciando processamento de {} batalhas para a equipe '{}'.", battleLogDTO.getItems().size(), teamInfo.getTeamName());

        Set<String> allPlayerTags = battleLogDTO.getItems().stream()
                .flatMap(info -> info.getBattle().getTeams().stream())
                .flatMap(List::stream)
                .map(player -> player.getTag().replace("#", ""))
                .collect(Collectors.toSet());

        Set<String> allBrawlerNames = battleLogDTO.getItems().stream()
                .flatMap(info -> info.getBattle().getTeams().stream())
                .flatMap(List::stream)
                .map(player -> player.getBrawler().getName())
                .collect(Collectors.toSet());

        Map<String, PlayerMODEL> existingPlayers = playerRepository.findAllById(allPlayerTags).stream()
                .collect(Collectors.toMap(PlayerMODEL::getTag, Function.identity()));
        Map<String, BrawlerMODEL> existingBrawlers = brawlerRepository.findAllById(allBrawlerNames).stream()
                .collect(Collectors.toMap(BrawlerMODEL::getName, Function.identity()));
        log.info("Buscados {} jogadores e {} brawlers existentes do banco de dados.", existingPlayers.size(), existingBrawlers.size());

        final Set<String> ourTeamRegisteredTags = teamInfo.getPlayersTags().stream()
                .map(tag -> tag.replace("#", ""))
                .collect(Collectors.toSet());

        for (BattleLogReceiveDTO.BattleLogInfo battleInfo : battleLogDTO.getItems()) {
            String battleTime = battleInfo.getBattleTime();
            if (battleMatchRepository.existsByBattleTime(battleTime)) {
                log.info("Batalha com o battleTime {} já existe no banco. Pulando.", battleTime);
                continue;
            }

            BattleMatch battleMatch = createAndSaveBattleMatch(battleInfo);

            for (List<BattleLogReceiveDTO.Player> teamData : battleInfo.getBattle().getTeams()) {
                TeamMODEL teamToSave = new TeamMODEL();

                boolean isMyTeam = teamData.stream()
                        .anyMatch(p -> ourTeamRegisteredTags.contains(p.getTag().replace("#", "")));

                teamToSave.setTeamType(isMyTeam ? TeamType.MY_TEAM : TeamType.ENEMY_TEAM);
                if (isMyTeam) {
                    teamToSave.setNameTeam(teamInfo.getTeamName());
                } else {
                    teamToSave.setNameTeam("Oponente");
                }

                final TeamMODEL savedTeam = teamRepository.save(teamToSave);

                savedTeam.getBattles().add(battleMatch);

                List<PlayerPerformanceMODEL> performances = teamData.stream()
                        .map(playerData -> createPlayerPerformance(playerData, savedTeam, existingPlayers, existingBrawlers))
                        .collect(Collectors.toList());

                playerPerformanceRepository.saveAll(performances);
            }
        }
        log.info("Processadas e salvas com sucesso {} batalhas.", battleLogDTO.getItems().size());
    }

    private PlayerMODEL findOrCreatePlayer(String tag, String name, Map<String, PlayerMODEL> cache) {
        String cleanedTag = tag.replace("#", "");
        return cache.computeIfAbsent(cleanedTag, t -> {
            log.debug("Criando novo jogador no banco: {}", cleanedTag);
            PlayerMODEL newPlayer = new PlayerMODEL();
            newPlayer.setTag(cleanedTag);
            newPlayer.setName(name);
            return playerRepository.save(newPlayer);
        });
    }

    private BrawlerMODEL findOrCreateBrawler(String name, Map<String, BrawlerMODEL> cache) {
        return cache.computeIfAbsent(name, n -> {
            log.debug("Criando novo brawler no banco: {}", name);
            BrawlerMODEL newBrawler = new BrawlerMODEL();
            newBrawler.setName(name);
            return brawlerRepository.save(newBrawler);
        });
    }

    private BattleMatch createAndSaveBattleMatch(BattleLogReceiveDTO.BattleLogInfo info) {
        BattleMatch battleMatch = new BattleMatch();
        battleMatch.setBattleTime(info.getBattleTime());
        battleMatch.setMode(info.getBattle().getMode());
        battleMatch.setType(info.getBattle().getType());
        battleMatch.setResult(info.getBattle().getResult());
        battleMatch.setDuration(info.getBattle().getDuration());
        return battleMatchRepository.save(battleMatch);
    }

    private PlayerPerformanceMODEL createPlayerPerformance(BattleLogReceiveDTO.Player playerData,
                                                           TeamMODEL team,
                                                           Map<String, PlayerMODEL> playerCache,
                                                           Map<String, BrawlerMODEL> brawlerCache) {

        PlayerMODEL player = findOrCreatePlayer(playerData.getTag(), playerData.getName(), playerCache);
        BrawlerMODEL brawler = findOrCreateBrawler(playerData.getBrawler().getName(), brawlerCache);

        PlayerPerformanceMODEL performance = new PlayerPerformanceMODEL();
        performance.setTeam(team);
        performance.setPlayer(player);
        performance.setBrawler(brawler);
        return performance;
    }
}