package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.*;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrawlDataService {

    private final PlayerRepository playerRepository;
    private final BrawlerRepository brawlerRepository;
    private final BattleMatchRepository battleMatchRepository;

    @Transactional
    public void processAndSaveBattleLog(BattleLogReceiveDTO battleLogDTO, Map<String, TeamRegisterMODEL> playerTagToTeamMap) {
        if (battleLogDTO == null || battleLogDTO.getItems() == null || battleLogDTO.getItems().isEmpty()) {
            return;
        }
        log.info("Iniciando o salvamento de {} batalhas filtradas.", battleLogDTO.getItems().size());

        List<String> allIncomingBattleTimes = battleLogDTO.getItems().stream().map(BattleLogReceiveDTO.BattleLogInfo::getBattleTime).toList();
        Set<String> existingBattleTimes = battleMatchRepository.findExistingBattleTimes(allIncomingBattleTimes);
        List<BattleLogReceiveDTO.BattleLogInfo> newBattlesToProcess = battleLogDTO.getItems().stream()
                .filter(info -> !existingBattleTimes.contains(info.getBattleTime()))
                .toList();

        if (newBattlesToProcess.isEmpty()) {
            log.info("Todas as batalhas recebidas já existem no banco de dados.");
            return;
        }

        Map<String, PlayerMODEL> playerCache = findAndCreatePlayersInBatch(newBattlesToProcess);
        Map<String, BrawlerMODEL> brawlerCache = findAndCreateBrawlersInBatch(newBattlesToProcess);

        List<BattleMatch> battlesToSave = new ArrayList<>();
        for (BattleLogReceiveDTO.BattleLogInfo battleInfo : newBattlesToProcess) {
            BattleMatch battleMatch = createBattleMatchEntity(battleInfo);

            List<MatchTeamMODEL> teamsInThisBattle = new ArrayList<>();
            for (List<BattleLogReceiveDTO.Player> teamData : battleInfo.getBattle().getTeams()) {
                MatchTeamMODEL matchTeam = createMatchTeamEntity(teamData, playerTagToTeamMap, playerCache, brawlerCache, battleMatch);
                teamsInThisBattle.add(matchTeam);
            }
            battleMatch.setTeams(teamsInThisBattle);
            battlesToSave.add(battleMatch);
        }

        battleMatchRepository.saveAll(battlesToSave);
        log.info("Salvas com sucesso {} novas batalhas.", battlesToSave.size());
    }

    private MatchTeamMODEL createMatchTeamEntity(List<BattleLogReceiveDTO.Player> teamData,
                                                 Map<String, TeamRegisterMODEL> playerTagToTeamMap,
                                                 Map<String, PlayerMODEL> playerCache,
                                                 Map<String, BrawlerMODEL> brawlerCache,
                                                 BattleMatch battleMatch) {
        MatchTeamMODEL matchTeam = new MatchTeamMODEL();
        TeamRegisterMODEL identifiedTeam = null;

        // Procura por qualquer jogador desta escalação no nosso mapa de times rastreados
        for (BattleLogReceiveDTO.Player player : teamData) {
            String cleanTag = player.getTag().replace("#", "");
            if (playerTagToTeamMap.containsKey(cleanTag)) {
                identifiedTeam = playerTagToTeamMap.get(cleanTag);
                break; // Encontramos o time, não precisa procurar mais
            }
        }

        if (identifiedTeam != null) {
            // Se encontramos o time no mapa, ele é um time RASTREADO. Usamos seu nome oficial.
            matchTeam.setNameTeam(identifiedTeam.getName());
            matchTeam.setTeamType(TeamType.TRACKED);
        } else {
            // Se não, é um time desconhecido.
            matchTeam.setNameTeam("Unknown");
            matchTeam.setTeamType(TeamType.UNKNOWN);
        }

        matchTeam.setBattle(battleMatch);

        List<PlayerPerformanceMODEL> performances = teamData.stream()
                .map(playerData -> createPlayerPerformance(playerData, matchTeam, playerCache, brawlerCache))
                .collect(Collectors.toList());
        matchTeam.setPlayers(performances);
        return matchTeam;
    }

    // O resto da classe (findAndCreatePlayersInBatch, etc.) continua igual...
    private PlayerPerformanceMODEL createPlayerPerformance(BattleLogReceiveDTO.Player playerData, MatchTeamMODEL matchTeam, Map<String, PlayerMODEL> playerCache, Map<String, BrawlerMODEL> brawlerCache) {
        String cleanedTag = playerData.getTag().replace("#", "");
        PlayerMODEL player = playerCache.get(cleanedTag);
        BrawlerMODEL brawler = brawlerCache.get(playerData.getBrawler().getName());

        PlayerPerformanceMODEL performance = new PlayerPerformanceMODEL();
        performance.setTeam(matchTeam);
        performance.setPlayer(player);
        performance.setBrawler(brawler);
        return performance;
    }

    private BattleMatch createBattleMatchEntity(BattleLogReceiveDTO.BattleLogInfo info) {
        BattleMatch battleMatch = new BattleMatch();
        battleMatch.setBattleTime(info.getBattleTime());
        battleMatch.setMode(info.getBattle().getMode());
        battleMatch.setType(info.getBattle().getType());
        battleMatch.setResult(info.getBattle().getResult());
        battleMatch.setDuration(info.getBattle().getDuration());
        return battleMatch;
    }

    private Stream<BattleLogReceiveDTO.Player> streamAllPlayers(List<BattleLogReceiveDTO.BattleLogInfo> battles) {
        return battles.stream()
                .map(BattleLogReceiveDTO.BattleLogInfo::getBattle)
                .filter(Objects::nonNull)
                .map(BattleLogReceiveDTO.Battle::getTeams)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .flatMap(List::stream);
    }

    private Map<String, PlayerMODEL> findAndCreatePlayersInBatch(List<BattleLogReceiveDTO.BattleLogInfo> newBattles) {
        Set<String> allPlayerTags = streamAllPlayers(newBattles)
                .map(p -> p.getTag().replace("#", ""))
                .collect(Collectors.toSet());

        Map<String, PlayerMODEL> existingPlayers = playerRepository.findAllById(allPlayerTags).stream()
                .collect(Collectors.toMap(PlayerMODEL::getTag, Function.identity()));

        List<PlayerMODEL> newPlayersToSave = streamAllPlayers(newBattles)
                .filter(pDTO -> !existingPlayers.containsKey(pDTO.getTag().replace("#", "")))
                .collect(Collectors.toMap(p -> p.getTag().replace("#", ""), Function.identity(), (p1, p2) -> p1))
                .values().stream()
                .map(pDTO -> {
                    PlayerMODEL newPlayer = new PlayerMODEL();
                    newPlayer.setTag(pDTO.getTag().replace("#", ""));
                    newPlayer.setName(pDTO.getName());
                    return newPlayer;
                }).toList();

        if (!newPlayersToSave.isEmpty()) {
            playerRepository.saveAll(newPlayersToSave);
            log.info("Criados {} novos jogadores no banco.", newPlayersToSave.size());
            newPlayersToSave.forEach(p -> existingPlayers.put(p.getTag(), p));
        }
        return existingPlayers;
    }

    private Map<String, BrawlerMODEL> findAndCreateBrawlersInBatch(List<BattleLogReceiveDTO.BattleLogInfo> newBattles) {
        Set<String> allBrawlerNames = streamAllPlayers(newBattles)
                .map(p -> p.getBrawler().getName())
                .collect(Collectors.toSet());

        Map<String, BrawlerMODEL> existingBrawlers = brawlerRepository.findAllById(allBrawlerNames).stream()
                .collect(Collectors.toMap(BrawlerMODEL::getName, Function.identity()));

        List<BrawlerMODEL> newBrawlersToSave = allBrawlerNames.stream()
                .filter(name -> !existingBrawlers.containsKey(name))
                .map(name -> {
                    BrawlerMODEL newBrawler = new BrawlerMODEL();
                    newBrawler.setName(name);
                    return newBrawler;
                }).toList();

        if (!newBrawlersToSave.isEmpty()) {
            brawlerRepository.saveAll(newBrawlersToSave);
            log.info("Criados {} novos brawlers no banco.", newBrawlersToSave.size());
            newBrawlersToSave.forEach(b -> existingBrawlers.put(b.getName(), b));
        }
        return existingBrawlers;
    }
}