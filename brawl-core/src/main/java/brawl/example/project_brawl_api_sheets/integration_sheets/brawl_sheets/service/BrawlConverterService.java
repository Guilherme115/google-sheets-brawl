package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.BattleLog;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity.Battle;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity.Player;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity.Team;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrawlConverterService {

    public List<Battle> converToEntity (BattleLog dto) {
        List<Battle> battles = new ArrayList<>();

        for (BattleLog.BattleLogInfo log : dto.getItems()) {
            BattleLog.Battle dtoBattle = log.getBattle();

            Battle battle = new Battle();
            battle.setMode(dtoBattle.getMode());
            battle.setType(dtoBattle.getType());
            battle.setResult(dtoBattle.getResult());
            battle.setDuration(dtoBattle.getDuration());

            List<Team> teamEntitities = new ArrayList<>();
            int teamIndex = 0;

            for (List<BattleLog.Player> dtoTeam : dtoBattle.getTeams()) {
                Team team = new Team();
                team.setIndex(teamIndex++);
                team.setMyTeam(teamIndex == 0);
                team.setBattle(battle);

                List<Player> players = new ArrayList<>();
                for(BattleLog.Player dtoPlayer : dtoTeam) {
                    Player player = new Player();
                    player.setName(dtoPlayer.getName());
                    player.setTag(dtoPlayer.getTag());
                    player.setBrawler(dtoPlayer.getBrawler().getName());
                    player.setTeam(team);
                    players.add(player);
                }
                team.setPlayers(players);
                teamEntitities.add(team);
                }
            battle.setTeams(teamEntitities);
            battles.add(battle);

            }
        return battles;

        }
    }

