package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.*;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime; // <-- IMPORT NECESSÁRIO
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    private final MatchSetRepository matchSetRepository;

    private static final DateTimeFormatter BRAWL_API_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSS'Z'").withZone(java.time.ZoneOffset.UTC);

    @Transactional
    public void processAndSaveBattleLog(BattleLogReceiveDTO battleLogDTO, Map<String, TeamRegisterMODEL> playerTagToTeamMap) {
        if (battleLogDTO == null || battleLogDTO.getItems() == null || battleLogDTO.getItems().isEmpty()) {
            return;
        }

        // --- CORREÇÃO 1: Otimização da verificação de duplicatas usando LocalDateTime ---
        // Mapeia as batalhas recebidas para um mapa com a data/hora parseada como chave.
        // Isso evita parsear a mesma string de data múltiplas vezes.
        Map<LocalDateTime, BattleLogReceiveDTO.BattleLogInfo> incomingBattlesMap = battleLogDTO.getItems().stream()
                .collect(Collectors.toMap(
                        info -> ZonedDateTime.parse(info.getBattleTime(), BRAWL_API_DATE_FORMATTER).toLocalDateTime(),
                        Function.identity(),
                        (existing, replacement) -> existing // Em caso de duplicatas na lista de entrada, mantém a primeira.
                ));

        // Busca no banco de dados apenas as datas que já existem.
        // IMPORTANTE: O método no repositório deve ser alterado para aceitar List<LocalDateTime>.
        Set<LocalDateTime> existingBattleDateTimes = battleMatchRepository.findExistingBattleTimes(new ArrayList<>(incomingBattlesMap.keySet()));

        // Remove do mapa as batalhas que já foram salvas no banco.
        existingBattleDateTimes.forEach(incomingBattlesMap::remove);

        // As batalhas restantes no mapa são as genuinamente novas.
        List<BattleLogReceiveDTO.BattleLogInfo> newBattlesToProcess = incomingBattlesMap.values().stream()
                .sorted(Comparator.comparing(BattleLogReceiveDTO.BattleLogInfo::getBattleTime))
                .collect(Collectors.toList());

        if (newBattlesToProcess.isEmpty()) {
            log.info("Nenhuma batalha genuinamente nova para processar.");
            return;
        }

        List<List<BattleLogReceiveDTO.BattleLogInfo>> battleSets = groupBattlesIntoSets(newBattlesToProcess);
        log.info("Encontradas {} batalhas novas, agrupadas em {} sets/partidas avulsas.", newBattlesToProcess.size(), battleSets.size());

        Map<String, PlayerMODEL> playerCache = findAndCreatePlayersInBatch(newBattlesToProcess);
        Map<String, BrawlerMODEL> brawlerCache = findAndCreateBrawlersInBatch(newBattlesToProcess);

        for (List<BattleLogReceiveDTO.BattleLogInfo> setOfBattles : battleSets) {
            MatchSet matchSet = createMatchSetEntity(setOfBattles, playerTagToTeamMap);

            for (BattleLogReceiveDTO.BattleLogInfo battleInfo : setOfBattles) {
                // A entidade BattleMatch agora será criada com o tipo LocalDateTime correto.
                BattleMatch battleMatch = createBattleMatchEntity(battleInfo);
                battleMatch.setMatchSet(matchSet);
                matchSet.getBattles().add(battleMatch);

                List<MatchTeamMODEL> teamsInThisBattle = new ArrayList<>();
                if (battleInfo.getBattle() != null && battleInfo.getBattle().getTeams() != null) {
                    for (List<BattleLogReceiveDTO.Player> teamData : battleInfo.getBattle().getTeams()) {
                        MatchTeamMODEL matchTeam = createMatchTeamEntity(teamData, playerTagToTeamMap, playerCache, brawlerCache, battleMatch);
                        teamsInThisBattle.add(matchTeam);
                    }
                }
                battleMatch.setTeams(teamsInThisBattle);
            }
            matchSetRepository.save(matchSet);
        }
        log.info("Salvos com sucesso {} novos sets.", battleSets.size());
    }

    // --- CORREÇÃO 2: Conversão da data antes de salvar a entidade ---
    private BattleMatch createBattleMatchEntity(BattleLogReceiveDTO.BattleLogInfo info) {
        BattleMatch battleMatch = new BattleMatch();

        // Parseia a string da API para um objeto ZonedDateTime.
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(info.getBattleTime(), BRAWL_API_DATE_FORMATTER);
        // Converte para LocalDateTime (sem fuso horário) para ser salvo no banco.
        battleMatch.setBattleTime(zonedDateTime.toLocalDateTime());

        battleMatch.setMode(info.getBattle().getMode());
        battleMatch.setType(info.getBattle().getType());
        battleMatch.setResult(info.getBattle().getResult());
        battleMatch.setDuration(info.getBattle().getDuration());
        battleMatch.setMap(info.getBattle().getMap());
        return battleMatch;
    }

    private List<List<BattleLogReceiveDTO.BattleLogInfo>> groupBattlesIntoSets(List<BattleLogReceiveDTO.BattleLogInfo> battles) {
        if (battles.isEmpty()) return Collections.emptyList();
        List<List<BattleLogReceiveDTO.BattleLogInfo>> sets = new ArrayList<>();
        List<BattleLogReceiveDTO.BattleLogInfo> currentSet = new ArrayList<>();
        currentSet.add(battles.get(0));
        for (int i = 1; i < battles.size(); i++) {
            BattleLogReceiveDTO.BattleLogInfo previous = battles.get(i - 1);
            BattleLogReceiveDTO.BattleLogInfo current = battles.get(i);
            if (arePlayersTheSame(previous, current) && areBattlesCloseInTime(previous, current, 5)) {
                currentSet.add(current);
            } else {
                sets.add(new ArrayList<>(currentSet));
                currentSet.clear();
                currentSet.add(current);
            }
        }
        sets.add(new ArrayList<>(currentSet));
        return sets;
    }

    private MatchSet createMatchSetEntity(List<BattleLogReceiveDTO.BattleLogInfo> battles, Map<String, TeamRegisterMODEL> playerTagToTeamMap) {
        BattleLogReceiveDTO.BattleLogInfo firstBattle = battles.get(0);
        ZonedDateTime startTime = ZonedDateTime.parse(firstBattle.getBattleTime(), BRAWL_API_DATE_FORMATTER);

        MatchSet set = new MatchSet();
        set.setSetStartTime(startTime.toLocalDateTime());

        if (firstBattle.getBattle() == null || firstBattle.getBattle().getTeams() == null || firstBattle.getBattle().getTeams().size() < 2) {
            set.setTeamAName("N/A");
            set.setTeamBName("N/A");
            set.setFinalResult("1-0"); // ou outro resultado padrão
            set.setWinningTeamName("N/A");
            return set;
        }

        List<BattleLogReceiveDTO.Player> teamAData = firstBattle.getBattle().getTeams().get(0);
        List<BattleLogReceiveDTO.Player> teamBData = firstBattle.getBattle().getTeams().get(1);
        String teamAName = getTeamNameFromPlayers(teamAData, playerTagToTeamMap);
        String teamBName = getTeamNameFromPlayers(teamBData, playerTagToTeamMap);
        int teamAScore = 0;
        int teamBScore = 0;

        for (BattleLogReceiveDTO.BattleLogInfo battle : battles) {
            String result = battle.getBattle().getResult();
            // A API define o resultado da perspectiva do jogador que fez a busca.
            // Para obter um placar correto de time vs time, você pode precisar de uma lógica mais complexa
            // ou assumir que o "Time A" é sempre o time do jogador buscado.
            // Assumindo que a primeira equipe na lista é sempre a que tem o "result" ('victory'/'defeat').
            if ("victory".equalsIgnoreCase(result)) teamAScore++;
            else if ("defeat".equalsIgnoreCase(result)) teamBScore++;
            // Lidar com "draw" se necessário.
        }

        set.setTeamAName(teamAName);
        set.setTeamBName(teamBName);
        set.setFinalResult(teamAScore + "-" + teamBScore);
        set.setWinningTeamName(teamAScore > teamBScore ? teamAName : (teamBScore > teamAScore ? teamBName : "DRAW"));
        return set;
    }

    private boolean arePlayersTheSame(BattleLogReceiveDTO.BattleLogInfo b1, BattleLogReceiveDTO.BattleLogInfo b2) {
        if (b1.getBattle() == null || b2.getBattle() == null || b1.getBattle().getTeams() == null || b2.getBattle().getTeams() == null) return false;
        Set<String> players1 = b1.getBattle().getTeams().stream().flatMap(List::stream).map(BattleLogReceiveDTO.Player::getTag).collect(Collectors.toSet());
        Set<String> players2 = b2.getBattle().getTeams().stream().flatMap(List::stream).map(BattleLogReceiveDTO.Player::getTag).collect(Collectors.toSet());
        return !players1.isEmpty() && players1.equals(players2);
    }

    private boolean areBattlesCloseInTime(BattleLogReceiveDTO.BattleLogInfo b1, BattleLogReceiveDTO.BattleLogInfo b2, int minutes) {
        try {
            ZonedDateTime time1 = ZonedDateTime.parse(b1.getBattleTime(), BRAWL_API_DATE_FORMATTER);
            ZonedDateTime time2 = ZonedDateTime.parse(b2.getBattleTime(), BRAWL_API_DATE_FORMATTER);
            return Duration.between(time1, time2).toMinutes() < minutes;
        } catch (Exception e) {
            log.warn("Não foi possível parsear a data para verificar a proximidade: {}", e.getMessage());
            return false;
        }
    }

    private String getTeamNameFromPlayers(List<BattleLogReceiveDTO.Player> players, Map<String, TeamRegisterMODEL> map) {
        return players.stream()
                .map(p -> p.getTag().replace("#", ""))
                .filter(map::containsKey)
                .findFirst()
                .map(map::get)
                .map(TeamRegisterMODEL::getName)
                .orElse("Unknown");
    }

    private MatchTeamMODEL createMatchTeamEntity(List<BattleLogReceiveDTO.Player> teamData,
                                                 Map<String, TeamRegisterMODEL> playerTagToTeamMap,
                                                 Map<String, PlayerMODEL> playerCache,
                                                 Map<String, BrawlerMODEL> brawlerCache,
                                                 BattleMatch battleMatch) {
        MatchTeamMODEL matchTeam = new MatchTeamMODEL();
        String teamName = getTeamNameFromPlayers(teamData, playerTagToTeamMap);
        matchTeam.setNameTeam(teamName);
        matchTeam.setTeamType("Unknown".equals(teamName) ? TeamType.UNKNOWN : TeamType.TRACKED);
        matchTeam.setBattle(battleMatch);
        List<PlayerPerformanceMODEL> performances = teamData.stream()
                .map(playerData -> createPlayerPerformance(playerData, matchTeam, playerCache, brawlerCache))
                .collect(Collectors.toList());
        matchTeam.setPlayers(performances);
        return matchTeam;
    }

    private PlayerPerformanceMODEL createPlayerPerformance(BattleLogReceiveDTO.Player playerData, MatchTeamMODEL matchTeam, Map<String, PlayerMODEL> playerCache, Map<String, BrawlerMODEL> brawlerCache) {
        PlayerMODEL player = playerCache.get(playerData.getTag().replace("#", ""));
        BrawlerMODEL brawler = brawlerCache.get(playerData.getBrawler().getName());
        PlayerPerformanceMODEL performance = new PlayerPerformanceMODEL();
        performance.setTeam(matchTeam);
        performance.setPlayer(player);
        performance.setBrawler(brawler);
        return performance;
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

        if (allPlayerTags.isEmpty()) return Collections.emptyMap();

        Map<String, PlayerMODEL> existingPlayers = playerRepository.findAllById(allPlayerTags).stream()
                .collect(Collectors.toMap(PlayerMODEL::getTag, Function.identity(), (e, r) -> e));

        List<PlayerMODEL> newPlayersToSave = streamAllPlayers(newBattles)
                .filter(pDTO -> pDTO.getName() != null)
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
            newPlayersToSave.forEach(p -> existingPlayers.put(p.getTag(), p));
        }
        return existingPlayers;
    }

    private Map<String, BrawlerMODEL> findAndCreateBrawlersInBatch(List<BattleLogReceiveDTO.BattleLogInfo> newBattles) {
        Set<String> allBrawlerNames = streamAllPlayers(newBattles)
                .map(p -> p.getBrawler().getName())
                .collect(Collectors.toSet());

        if (allBrawlerNames.isEmpty()) return Collections.emptyMap();

        Map<String, BrawlerMODEL> existingBrawlers = brawlerRepository.findAllById(allBrawlerNames).stream()
                .collect(Collectors.toMap(BrawlerMODEL::getName, Function.identity(), (e, r) -> e));

        List<BrawlerMODEL> newBrawlersToSave = allBrawlerNames.stream()
                .filter(name -> !existingBrawlers.containsKey(name))
                .map(name -> {
                    BrawlerMODEL newBrawler = new BrawlerMODEL();
                    newBrawler.setName(name);
                    return newBrawler;
                }).toList();

        if (!newBrawlersToSave.isEmpty()) {
            brawlerRepository.saveAll(newBrawlersToSave);
            newBrawlersToSave.forEach(b -> existingBrawlers.put(b.getName(), b));
        }
        return existingBrawlers;
    }
}